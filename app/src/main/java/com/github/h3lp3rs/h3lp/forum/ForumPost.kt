package com.github.h3lp3rs.h3lp.forum

import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.util.concurrent.CompletableFuture

/**
 * A forum post containing all the data.
 * The class has as attribute the instance of the forum that directly points on it
 * @param forum Forum at the path of the post
 * @param post Data of the post
 */
class ForumPost(private val forum: Forum, private val post: ForumPostData) {

    /**
     * Adds a reply to the post
     * @param author The author of the reply
     * @param content The content of the reply
     * @return this This post updated with the new reply (and potential replies from others)
     * in the form of a future
     */
    fun reply(author: String, content: String): CompletableFuture<ForumPost> {
        return forum.newPost(author, content)
    }

    /**
     * Refreshes the post if possible
     * @return this This post updated with potential replies from other
     * in the form of a future
     */
    fun refresh(): CompletableFuture<ForumPost> {
        return forum.getPost(emptyList())
    }

    /**
     * Listens to this post and executes the lambda with the new data
     * as parameter when a change occurs (ie: post added)
     * @param action The action taken when a change occurs
     */
    fun listen(action: (ForumPostData) -> Unit) {
        forum.listenToAll(action)
    }
}
