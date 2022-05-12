package com.github.h3lp3rs.h3lp.forum

import android.os.Build
import androidx.annotation.RequiresApi
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

    // JSON-able data classes that will be stored
    data class CachePost(
        val postData: ForumPostData,
        val repliesData: ArrayList<ForumPostData>
    )

    // Entry for one category that contains all the posts
    data class CacheEntry(val posts: ArrayList<CachePost>)

    // Keeps track of all category paths where there are entries
    data class CacheHeader(val categoryPaths: ArrayList<String>)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun newPost(author: String, content: String): CompletableFuture<ForumPost> {
        // Cache the post if successfully returned
        return forum.newPost(author, content).thenApply {
            updateCacheWithPost(it)
            it
        }
    }

    override fun getPost(relativePath: Path): CompletableFuture<ForumPost> {
        // Cache the post if successfully found
        return forum.getPost(relativePath).thenApply {
            updateCacheWithPost(it)
            it
        }.exceptionally { // Fetch cache for post if not found
            // Get the pointer of the category just above the required post
            val cacheLoc = CachedForum(forum.child(relativePath.dropLast(1)))
            // Get the post unique key
            val key = relativePath.last()

            val posts = cacheLoc.getCacheEntry().posts
            // Search for corresponding post
            for (p in posts) {
                if (p.postData.key == key) {
                    return@exceptionally ForumPost(cacheLoc, p.postData, p.repliesData)
                }
            }
            throw it
        }
    }

    override fun getAll(): CompletableFuture<List<CategoryPosts>> {
        // Cache the posts if successfully returned
        return forum.getAll().thenApply {
            for (categoryPosts in it) {
                val subForum = CachedForum(forum.root().child(categoryPosts.first))
                for (post in categoryPosts.second) {
                    subForum.updateCacheWithPost(post)
                }
            }
            it
        }.exceptionally { // Fetch cache for posts if not found
            val list = ArrayList<CategoryPosts>()

            for (path in getCacheHeader().categoryPaths) {
                val listPath = path.split("/")
                val subForum = CachedForum(forum.root().child(listPath))
                val entry = subForum.getCacheEntry()

                list.add(Pair(listPath, entry.posts.stream().map { post ->
                    ForumPost(subForum, post.postData, post.repliesData)
                }.collect(toList())))

                return@exceptionally list
            }
            throw it
        }
    }

    private fun updateCacheWithPost(post: ForumPost) {
        // Copy for mutability issues
        val posts = ArrayList(getCacheEntry().posts)

        // Look for post with same key
        for (i in 0..posts.size) {
            val p = posts[i]
            if (p.postData.key == post.post.key) {
                posts[i] = CachePost(post.post, ArrayList(post.replies))
                setCacheEntry(CacheEntry(posts))
                return
            }
        }

        // First occurrence of key
        posts.add(CachePost(post.post, ArrayList(post.replies)))
        setCacheEntry(CacheEntry(posts))
    }

    private fun getCacheEntry(): CacheEntry {
        val cachePath = path.joinToString(separator = "/")

        // Never null due to non-null default parameter
        return cache.getObjectOrDefault(
            cachePath,
            CacheEntry::class.java, CacheEntry(ArrayList())
        )!!
    }

    private fun setCacheEntry(entry: CacheEntry) {
        val cachePath = path.joinToString(separator = "/")

        addCachePathIfNeeded(cachePath)

        cache.setObject(cachePath, CacheEntry::class.java, entry)
    }

    private fun addCachePathIfNeeded(cachePath: String) {
        // Copy for mutability issues
        val newPaths = ArrayList(getCacheHeader().categoryPaths)
        if (!newPaths.contains(cachePath)) {
            newPaths.add(cachePath)
        }

        // Add path to category posts
        cache.setObject(
            "$CACHE_HEADER//$cachePath", CacheHeader::class.java,
            CacheHeader(newPaths)
        )
        // Recursive call to update parent
        if (path.isNotEmpty()){
            CachedForum(forum.parent()).addCachePathIfNeeded(cachePath)
        }
    }

    private fun getCacheHeader(): CacheHeader {
        val cachePath = path.joinToString(separator = "/")

        // Never null due to non-null default parameter
        return cache.getObjectOrDefault(
            "$CACHE_HEADER//$cachePath",
            CacheHeader::class.java,
            CacheHeader(ArrayList())
        )!!
    }

    override fun listenToAll(action: (ForumPostData) -> Unit) {
        return forum.listenToAll(action)
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

    companion object {
        private const val CACHE_HEADER = "HEADER"
    }
}