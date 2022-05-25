package com.github.h3lp3rs.h3lp.forum.implementation

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.h3lp3rs.h3lp.forum.*
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors.toList

/**
 * This wrapper class forwards all calls to a Forum implementation and adds an additional
 * layer of caching. This way, negative responses due to connectivity issues still resolve
 * as valid old data output.
 * NOTE: This cache behaves according to the ForumProtocol.md limitations (subset of FireForum
 * interface)
 * @param forum The wrapped forum we want to cache
 */
class CachedForum(private val forum: Forum) : Forum {
    override val path = forum.path
    private val cache = storageOf(FORUM_CACHE)

    // Entry for all the posts at one path
    data class CacheEntry(val posts: ArrayList<ForumPostData>)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun newPost(
        author: String,
        content: String,
        isPost: Boolean
    ): CompletableFuture<ForumPost> {
        // Cache the post if successfully returned
        return forum.newPost(author, content, isPost).thenApply {
            val post = ForumPost(CachedForum(it.forum), it.post, it.replies)
            updateCacheWithPost(emptyList(), post.post)
            post
        }
    }

    override fun getPost(relativePath: Path): CompletableFuture<ForumPost> {
        // Cache the post if successfully found
        return forum.getPost(relativePath).thenApply {
            val post = ForumPost(CachedForum(it.forum), it.post, it.replies)
            updateCacheWithPost((path + relativePath).dropLast(1).drop(path.size), post.post)
            post
        }.exceptionally { exc -> // Fetch cache for post if not found
            // Get the pointer of the category just above the required post
            val cacheLoc = CachedForum(forum.child(relativePath.dropLast(1)))
            // Get the post unique key
            val key = relativePath.last()

            val posts = cacheLoc.getCacheEntry(emptyList()).posts
            var post: ForumPostData? = null
            // Search for corresponding post in the category
            for (p in posts) {
                if (p.key == key) {
                    post = p
                }
            }
            post?.let {
                val replies = cacheLoc.getCacheEntry(listOf(it.repliesKey)).posts
                return@exceptionally ForumPost(cacheLoc, it, replies)
            }

            throw exc
        }
    }

    override fun getAll(): CompletableFuture<List<CategoryPosts>> {
        // Cache the posts if successfully returned
        return forum.getAll().thenApply {
            // Transform the list to make it recursively cache compatible
            val cachedList = it.stream().map { l ->
                CategoryPosts(l.first, l.second.stream().map { p ->
                    ForumPost(CachedForum(p.forum), p.post, p.replies)
                }.collect(toList()))
            }.collect(toList())

            for (categoryPosts in cachedList) {
                val subForum = CachedForum(forum.root().child(categoryPosts.first))
                for (post in categoryPosts.second) {
                    subForum.updateCacheWithPost(emptyList(), post.post)
                }
            }
            // Fetch cache for posts if not found -> If fail we only get an empty list
            if (cachedList.isEmpty()) getAllAux() else cachedList
        }
    }

    private fun getAllAux(): List<CategoryPosts> {
        if (path.isEmpty()) {
            // In case we are in the root forum, get the CategoryPosts from all categories
            val ls = mutableListOf<CategoryPosts>()
            for (category in ForumCategory.values()) {
                val categoryForum = CachedForum(forum.child(category.name))
                ls.addAll(categoryForum.getAllAux())
            }

            return ls
        } else {
            // In case we are in a category, we only have a single CategoryPost to return (thus
            // we return a singleton list)
            val allPosts = mutableListOf<ForumPost>()
            val entry = getCacheEntry(emptyList()).posts
            for (post in entry) {
                val replies = getCacheEntry(listOf(post.repliesKey)).posts
                allPosts.add(ForumPost(this, post, replies))
            }

            return listOf(Pair(path, allPosts))
        }
    }

    private fun updateCacheWithPost(relativePath: Path, postData: ForumPostData) {
        // Copy for mutability issues
        val posts = ArrayList(getCacheEntry(relativePath).posts)

        // Look for post with same key
        for (i in 0 until posts.size) {
            val p = posts[i]
            if (p.key == postData.key) {
                posts[i] = postData
                setCacheEntry(relativePath, CacheEntry(posts))
                return
            }
        }

        // First occurrence of key
        posts.add(postData)
        setCacheEntry(relativePath, CacheEntry(posts))
    }

    private fun getCacheEntry(relativePath: Path): CacheEntry {
        val cachePath = (path + relativePath).joinToString(separator = "/")

        // Never null due to non-null default parameter
        return cache.getObjectOrDefault(
            cachePath,
            CacheEntry::class.java, CacheEntry(ArrayList())
        )!!
    }

    private fun setCacheEntry(relativePath: Path, entry: CacheEntry) {
        val cachePath = (path + relativePath).joinToString(separator = "/")
        cache.setObject(cachePath, CacheEntry::class.java, entry)
    }

    override fun listenToAll(action: (ForumPostData) -> Unit) {
        val newWrappedAction: (ForumPostData) -> Unit = {
            // This is a very hacky but easy way to know from the data, the path to its forum pointer
            // We should change the listener design to make it cleaner, but for now it works fine
            val path = listOf(it.category.name) + if(it.isPost) emptyList() else listOf(it.key)
            CachedForum(forum.root()).updateCacheWithPost(path, it)
            // Execute previous action
            action(it)
        }
        forum.listenToAll(newWrappedAction)
    }

    override fun root(): Forum {
        return CachedForum(forum.root())
    }

    override fun child(relativePath: Path): Forum {
        return CachedForum(forum.child(relativePath))
    }

    override fun parent(): Forum {
        return CachedForum(forum.parent())
    }
}