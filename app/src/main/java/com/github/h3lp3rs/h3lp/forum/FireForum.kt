import android.os.Build
import androidx.annotation.RequiresApi
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.FORUM
import com.github.h3lp3rs.h3lp.forum.*
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.time.ZonedDateTime
import java.util.concurrent.CompletableFuture

class FireForum(override val path: Path) : Forum {
    private val rootForum = databaseOf(FORUM)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun newPost(author: String, content: String): CompletableFuture<ForumPost> {
        var key = ""
        // Incrementing by 2 so that a post's replies key is always 1 more than a post's key (this
        // is also an optimization to avoid us requiring 2 calls to incrementAndGet)
        return rootForum.incrementAndGet(UNIQUE_POST_ID, 2).thenApply { postKey ->
            key = postKey.toString()
            val repliesKey = (postKey + 1).toString()

            val forumPostData = ForumPostData(author, content, ZonedDateTime.now(), key, repliesKey)
            rootForum.setObject(
                pathToKey(path + key),
                ForumPostData::class.java,
                forumPostData
            )
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
        return rootForum.getObject(key, ForumPostData::class.java).thenCompose { postData ->
            rootForum.getObjectsList(
                pathToKey(path + postData.repliesKey),
                ForumPostData::class.java
            )
                .thenApply { replies ->
                    ForumPost(parent(), postData, replies)
                }.handle { post, error ->
                    if (error != null) {
                        // If the post has no replies yet
                        ForumPost(parent(), postData, emptyList())
                    } else {
                        post
                    }
                }
        }
    }

    override fun getAll(): CompletableFuture<List<CategoryPosts>> {
        if (isRoot()) {
//            return getAllFromNextCategories(CompletableFuture.completedFuture(emptyList()), 0)
            var future: CompletableFuture<List<CategoryPosts>> =
                CompletableFuture.completedFuture(emptyList())
            // In case we are in the root forum
            for (category in ForumCategory.values()) {
                // For all categories, we add them to the list of category posts
                val categoryForum = forumOf(category)
                future = future.thenCompose { list ->
                    categoryForum.getAll().handle { it, error ->
                        // If there was no posts on that forum
                        if (error != null) {
                            list
                        } else {
                            list + it
                        }
                    }
                }
            }
            return future
        } else {
            // In case we are in a category
            return getAllFromCategory().thenApply { listOf(it) }
        }
    }

    private fun getAllFromCategory(): CompletableFuture<CategoryPosts> {
        val forumPostsFuture = rootForum
            .getObjectsList(pathToKey(path + POSTS_LIST), String::class.java)
            .thenApply { keyList ->
                keyList.map { rootForum.getObject(pathToKey(path + it), ForumPostData::class.java) }
            }
            .thenCompose {
                val cfs: Array<CompletableFuture<ForumPost>> =
                    it.map { futurePostData ->
                        futurePostData.thenCompose { postData ->
                            getPost(
                                listOf(postData.key)
                            )
                        }
                    }.toTypedArray()
                typedAllOf(*cfs)
            }

        return forumPostsFuture
            .thenApply { list ->
                // path.last contains the category name
                Pair(path.last(), list)
            }
    }

    private fun typedAllOf(vararg futures: CompletableFuture<ForumPost>?): CompletableFuture<List<ForumPost>> {
        return CompletableFuture.allOf(*futures)
            .thenApply {
                futures.map {
                    // The future is already joined (since allOf returned)
                        f ->
                    f!!.join()
                }
            }
    }

    override fun listenToAll(action: (ForumPostData) -> Unit) {
        // In case we are in the root forum
        when {
            isRoot() -> {
                for (category in ForumCategory.values()) {
                    // Listen to all posts of that category
                    forumOf(category).listenToAll(action)
                }
            }
            isCategory() -> {
                val onNewPost: (String) -> Unit = { postKey ->
                    getPost(listOf(postKey)).thenAccept { postForum ->
                        action(postForum.post)

                        // Also listen for all updates on the posts (when someone replies)
                        child(listOf(postForum.post.key)).listenToAll(action)
                    }
                }
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
                    rootForum.addEventListener(
                        pathToKey(repliesPath),
                        ForumPostData::class.java,
                        action
                    ) {}
                }
            }
        }
    }

    override fun root(): Forum {
        return FireForum(emptyList())
    }

    override fun child(relativePath: Path): Forum {
        return FireForum(path + relativePath)
    }

    /**
     * Returns the corresponding string (in Firebase, strings separated by a "/" represent a path,
     * as in a filesystem with "files" or children in the case of a database)
     */
    private fun pathToKey(path: Path): String {
        return path.joinToString(separator = "/")
    }

    /**
     * Abstracts away the implementation of the path into the Forum structure, here, the root is
     * represented by an empty path
     * @return A boolean corresponding to the fact that this forum is (or isn't) the root forum
     */
    private fun isRoot(): Boolean {
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

    //TODO : make actual method not private
    private fun parent(): Forum {
        if (isRoot()) {
            return this
        } else {
            return FireForum(path.dropLast(1))
        }
    }

    companion object {
        const val UNIQUE_POST_ID = "unique post id"
        const val POSTS_LIST = "posts"
    }
}
