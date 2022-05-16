package com.github.h3lp3rs.h3lp.forum.implementation

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.forum.*
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.time.ZonedDateTime
import java.util.concurrent.CompletableFuture

/**
 * This abstract forum behaves according to the ForumProtocol.md limitations (subset of Forum
 * interface) and uses our key-value database as an underlying data structure.
 *
 * @param rootForum An implementation of the underlying database acting as root of our forum.
 */
abstract class SimpleDBForum(private val rootForum: Database) : Forum {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun newPost(author: String, content: String): CompletableFuture<ForumPost> {
        var key = ""
        // Incrementing by 2 so that a post's replies key is always 1 more than a post's key (this
        // is also an optimization to avoid us requiring 2 calls to incrementAndGet)
        return rootForum.incrementAndGet(UNIQUE_POST_ID, 2).thenApply { postKey ->
            key = postKey.toString()
            val repliesKey = (postKey + 1).toString()

            val forumPostData = ForumPostData(
                author,
                content,
                ZonedDateTime.now(),
                key,
                repliesKey,
                getCurrentCategory()
            )
            rootForum.setObject(
                pathToKey(path + key),
                ForumPostData::class.java,
                forumPostData
            )

            // If the forum is a category, this means that the post is a "main post" (not a reply)
            // we thus add it to the posts in the database (as explained in the forum protocol)
            if (isCategory()) {
                rootForum.addToObjectsListConcurrently(
                    pathToKey(path + POSTS_LIST),
                    String::class.java,
                    key
                )
            }
            // We can't use getPost here since we aren't sure that the setObject succeeded yet
            ForumPost(this, forumPostData, emptyList())
        }
    }

    override fun getPost(relativePath: Path): CompletableFuture<ForumPost> {
        val fullPath = path + relativePath
        val key = pathToKey(fullPath)

        // Get the required post
        val postFuture = rootForum.getObject(key, ForumPostData::class.java)

        // Also get that post's replies
        return postFuture.thenCompose { postData ->
            // Get all the replies from that post
            rootForum.getObjectsList(
                pathToKey(path + postData.repliesKey),
                ForumPostData::class.java
            ).handle { replies, error ->
                if (error != null) {
                    // If the post has no replies yet
                    ForumPost(parent(), postData, emptyList())
                } else {
                    // If the post had replies, add them to the object
                    ForumPost(parent(), postData, replies)
                }
            }
        }
    }

    override fun getAll(): CompletableFuture<List<CategoryPosts>> {
        if (isRoot()) {
            // In case we are in the root forum, get the CategoryPosts from all categories
            var future: CompletableFuture<List<CategoryPosts>> =
                CompletableFuture.completedFuture(emptyList())
            for (category in ForumCategory.values()) {
                // For all categories, we add them to the list of category posts
                val categoryForum = ForumCategory.forumOf(category)
                future = future.thenCompose { list ->
                    // Recursively call getAll
                    categoryForum.getAll().handle { it, error ->
                        if (error != null) {
                            // If there is no posts on this forum, keep the current list of
                            // category posts
                            list
                        } else {
                            list + it
                        }
                    }
                }
            }
            return future
        } else {
            // In case we are in a category, we only have a single CategoryPost to return (thus
            // we return a singleton list)
            return getAllFromCategory().thenApply { listOf(it) }
        }
    }

    /**
     * @return Returns all the posts in this forum's category in a future
     */
    private fun getAllFromCategory(): CompletableFuture<CategoryPosts> {
        val forumPostsFuture = rootForum
            .getObjectsList(pathToKey(path + POSTS_LIST), String::class.java)
            .thenApply { keyList ->
                // For all posts in this category, get their forum post data
                keyList.map { rootForum.getObject(pathToKey(path + it), ForumPostData::class.java) }
            }
            .thenCompose {
                // We need an array since this is the type expected by the method allOf used in
                // typeAllOf
                val cfs: Array<CompletableFuture<ForumPost>> =
                    it.map { futurePostData ->
                        futurePostData.thenCompose { postData ->
                            // Get the forum post for each post in the category
                            getPost(
                                postData.key
                            )
                        }
                    }.toTypedArray()
                typedAllOf(*cfs)
            }

        // Transforming the future<list<ForumPost>> into the required format
        // future<Pair<Category name, list<ForumPost>>
        return forumPostsFuture
            .thenApply { list ->
                // path.last contains the category name
                Pair(listOf(path.last()), list)
            }
    }

    /**
     * Method to transform a list<future<T>> into future<list<T>>, is the typed equivalent of
     * CompletableFuture.allOf which would return future<list<Void>>
     * @param futures A list (in the form of a vararg as expected by allOf) of futures of posts
     * @return A future that completes with the value of all the futures in the "futures" list
     *  once they have completed
     */
    private fun typedAllOf(vararg futures: CompletableFuture<ForumPost>?): CompletableFuture<List<ForumPost>> {
        return CompletableFuture.allOf(*futures)
            .thenApply {
                futures.map {
                    // The futures are already all completed (since we are in the thenApply of allOf)
                    // thus the join call won't be a problem
                        f ->
                    f!!.join()
                }
            }
    }

    override fun listenToAll(action: (ForumPostData) -> Unit) {
        // In case we are in the root forum
        when {
            isRoot() -> {
                // Listen to all categories and to all posts in that category
                for (category in ForumCategory.values()) {
                    ForumCategory.forumOf(category).listenToAll(action)
                }
            }
            isCategory() -> {
                // Callback called on every new post in a category, calls "action" on that new post
                // and adds listeners for replies on that new post
                val onNewPost: (String) -> Unit = { postKey ->
                    getPost(postKey).thenAccept { postForum ->
                        action(postForum.post)
                        child(postForum.post.key).listenToAll(action)
                    }
                }
                // We add the listener on the POSTS_LIST key since as explained in the protocol,
                // it is updated with new post keys on every new post
                rootForum.addEventListener(
                    pathToKey(path + POSTS_LIST),
                    String::class.java,
                    onNewPost
                ) {}
            }
            else -> {
                // Listening to a single post means listening on all of its children
                getPost(emptyList()).thenAccept { forumPost ->
                    val repliesPath = forumPost.forum.path + forumPost.post.repliesKey
                    // Adding a listener on the replies key since this is where all replies to that
                    // post are stored
                    rootForum.addEventListener(
                        pathToKey(repliesPath),
                        ForumPostData::class.java,
                        action
                    ) {}
                }
            }
        }
    }

    /**
     * Translates a Path into its corresponding key as understood by Firebase (in Firebase, strings
     * separated by a "/" represent a path, as in a filesystem with "files" or children in the case
     * of a database)
     * @param path The path to transform into a string according to Firebase
     * @return The corresponding string
     */
    private fun pathToKey(path: Path): String {
        return path.joinToString(separator = "/")
    }

    /**
     * Abstracts away the implementation of the path into the Forum structure, here, the root is
     * represented by an empty path
     * @return A boolean corresponding to the fact that this forum is (or isn't) the root forum
     */
    protected fun isRoot(): Boolean {
        return path.isEmpty()
    }


    /**
     * Abstracts away the implementation of the path into the Forum structure, here, a category is
     * represented by a path of length 1
     * @return A boolean corresponding to the fact that this forum is (or isn't) the root forum
     */
    private fun isCategory(): Boolean {
        return path.size == 1
    }

    /**
     * @return The category of the current forum and the default category in case the
     * current forum is root
     */
    private fun getCurrentCategory(): ForumCategory {
        return if (!isRoot()) {
            ForumCategory.valueOf(path.first())
        } else {
            ForumCategory.DEFAULT_CATEGORY
        }
    }

    companion object {
        const val UNIQUE_POST_ID = "unique post id"
        const val POSTS_LIST = "posts"
    }
}