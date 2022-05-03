package com.github.h3lp3rs.h3lp.forum

import java.util.concurrent.CompletableFuture

/**
 * Conceptualizes a general-use forum
 */
interface Forum {

    // The current path to the forum instance (pointer)
    val path: String

    //----------------------//
    //-- Action functions --//
    //----------------------//

    /**
     * Creates a new post in the forum (at path level)
     * @param author The uid of the author
     * @param content The content of the post
     * @return this The newly created forum post
     */
    fun newPost(author: String, content: String): CompletableFuture<ForumPost>

    /**
     * Gets the entire post at the given relative path from here
     * The value is null if no such post exist
     * @param relativePath The relative path to the post
     * @return post The forum post in the form of a future
     */
    fun getPost(relativePath: String): CompletableFuture<ForumPost?>

    /**
     * Gets all the posts at this level
     * @return posts All posts at this level in the form of a future
     */
    fun getAllPosts(): CompletableFuture<List<ForumPost>>

    //--------------------------//
    //-- Navigation functions --//
    //--------------------------//

    /**
     * Returns the root of the forum
     * @return root The root of the forum
     */
    fun root(): Forum

    /**
     * Returns one of the main pre-defined categories sub-forum
     * @param category One of the pre-defined main categories
     * @return forum The forum of the given category
     */
    fun category(category: ForumCategory): Forum

    /**
     * Returns the child forum at the relative path from here
     * If it does not yet exist, it will be created automatically when
     * a first post is sent
     * @param relativePath The relative path to the child forum
     * @return forum The child forum
     */
    fun child(relativePath: String): Forum
}