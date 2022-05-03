package com.github.h3lp3rs.h3lp.forum

import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.util.concurrent.CompletableFuture

/**
 * A forum post containing all the data
 */
interface ForumPost {

    // Forum at the path of the post
    val forum: Forum
    // Data of the post
    val post: ForumPostData

    /**
     * Adds a reply to the post
     * @param author The author of the reply
     * @param content The content of the reply
     * @return this This post updated with the new reply (and potential replies from others)
     * in the form of a future
     */
    fun reply(author: String, content: String): CompletableFuture<ForumPost>

    /**
     * Refreshes the post if possible
     * @return this This post updated with potential replies from other
     * in the form of a future
     */
    fun refresh(): CompletableFuture<ForumPost>
}