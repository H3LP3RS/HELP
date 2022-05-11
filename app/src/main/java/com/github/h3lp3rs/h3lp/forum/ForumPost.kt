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
    val forum: Forum,
    val post: ForumPostData,
    val replies: List<ForumPostData>,
) {

    /**
     * Adds a reply to the post
     * @param author The author of the reply
     * @param content The content of the reply
     * @return This post in the form of a future
     * WARNING: the returned post doesn't necessarily contain the reply since we have no way of
     * knowing when the database actually got the reply
     */
    fun reply(author: String, content: String): CompletableFuture<ForumPost> {
        forum.child(post.repliesKey).newPost(author, content)
        return refresh()
    }

    /**
     * Refreshes the post if possible
     * @return this This post updated with potential replies from others
     * in the form of a future
     */
    fun refresh(): CompletableFuture<ForumPost> {
        return forum.child(post.key).getPost(emptyList())
    }

    /**
     * Listens to this post and executes the lambda with the new data
     * as parameter when a change occurs (ie: reply added)
     * @param action The action taken when a change occurs
     */
    fun listen(action: (ForumPostData) -> Unit) {
        forum.child(post.key).listenToAll(action)
    }
}
