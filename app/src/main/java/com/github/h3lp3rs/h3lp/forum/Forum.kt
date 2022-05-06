package com.github.h3lp3rs.h3lp.forum

import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.util.concurrent.CompletableFuture

/**
 * Represents all the posts at one level path (or category), the posts are in chronological order
 */
typealias CategoryPosts = Pair<String, List<ForumPost>>

/**
 * The path to a forum, every sub-category / post is represented in a string, with the root of the
 * forum being an empty list
 */
typealias Path = List<String>

/**
 * Conceptualizes a general-use forum. This abstraction is in reality
 * more of a forum pointer than a real forum with data.
 * It offers the advantages to work asynchronously, the navigation can
 * indeed be done without any direct access and simply buffered before a
 * real write (or read) takes place.
 * - Note: This interface cannot write on or read a specific post, this is delegated to
 * the ForumPost interface.
 */
interface Forum {

    // The current path to the forum instance (pointer)
    val path: Path

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
     * The future fails exceptionally if no post exists
     * @param relativePath The relative path to the post
     * @return post The forum post in the form of a future
     */
    fun getPost(relativePath: Path): CompletableFuture<ForumPost>

    /**
     * Gets all the posts at this path and below
     * @return posts All posts at this level and below in the form of a future
     * The pair is a path -> posts relationship
     * Note: Kotlin not supporting pattern matching, using a recursive definition
     * is in reality more cumbersome
     */
    fun getAll(): CompletableFuture<List<CategoryPosts>>

    /**
     * Listens to all posts at this level and executes a common lambda on
     * the new specific post data (for every existing post, and newly created ones)
     * @param action The action taken when a post change/add occurs
     */
    fun listenToAll(action: (ForumPostData) -> Unit)

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
    fun child(relativePath: Path): Forum
}