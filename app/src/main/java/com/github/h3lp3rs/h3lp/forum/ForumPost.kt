package com.github.h3lp3rs.h3lp.forum

import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.util.concurrent.CompletableFuture

/**
 * A forum post containing all the data.
 * The class has as attribute the instance of the forum that directly points on it
 * @param forum Forum at the path of the post
 * @param post Data of the post
 * @param replies Replies to this post
 */
class ForumPost(
    private val forum: Forum,
    val post: ForumPostData,
    val replies: List<ForumPostData>,
) {

    /**
     * Adds a reply to the post
     * @param author The author of the reply
     * @param content The content of the reply
     * @return This post updated with the new reply (and potential replies from others)
     * in the form of a future
     */
    fun reply(author: String, content: String): CompletableFuture<ForumPost> {
        forum.child(listOf(post.repliesKey)).newPost(author, content)
        return refresh()
    }

    /**
     * Refreshes the post if possible
     * @return this This post updated with potential replies from others
     * in the form of a future
     */
    fun refresh(): CompletableFuture<ForumPost> {
        return forum.child(listOf(post.key)).getPost(emptyList())
    }

    /**
     * Listens to this post and executes the lambda with the new data
     * as parameter when a change occurs (ie: reply added)
     * @param action The action taken when a change occurs
     */
    fun listen(action: (ForumPostData) -> Unit) {
        forum.listenToAll(action)
    }
}
