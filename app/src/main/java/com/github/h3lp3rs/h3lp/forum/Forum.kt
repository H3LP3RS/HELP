package com.github.h3lp3rs.h3lp.forum

import android.content.Context
import android.content.Intent
import com.github.h3lp3rs.h3lp.dataclasses.MedicalType
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.github.h3lp3rs.h3lp.notification.NotificationService
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.sendIntentNotification
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getName
import com.github.h3lp3rs.h3lp.storage.Storages
import java.util.concurrent.CompletableFuture

/**
 * Represents all the posts at one level path (or category), the posts are in chronological order
 */
typealias CategoryPosts = Pair<Path, List<ForumPost>>

const val THEME_KEY = "forumThemeKey"

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
    val path : Path

    //----------------------//
    //-- Action functions --//
    //----------------------//

    /**
     * Creates a new post in the forum (at path level)
     * @param author The uid of the author
     * @param content The content of the post
     * @param isPost Whether this is a post or a reply to a post
     * @return The newly created forum post
     */
    fun newPost(author : String, content : String, isPost : Boolean) : CompletableFuture<ForumPost>

    /**
     * Gets the entire post at the given relative path from here
     * The future fails exceptionally if no post exists
     * @param relativePath The relative path to the post
     * @return The forum post in the form of a future
     */
    fun getPost(relativePath : Path) : CompletableFuture<ForumPost>


    /**
     * Overrides getPost to get the post on the child forum
     * Since this is the most common use-case of getPost, will make calls cleaner
     * @param child The child where the post is located
     * @return The forum post in the form of a future
     */
    fun getPost(child : String) : CompletableFuture<ForumPost> {
        return getPost(listOf(child))
    }

    /**
     * Gets all the posts at this path and below
     * @return posts All posts at this level and below in the form of a future
     * The pair is a path -> posts relationship
     * Note: Kotlin not supporting pattern matching, using a recursive definition
     * is in reality more cumbersome
     */
    fun getAll() : CompletableFuture<List<CategoryPosts>>

    /**
     * Listens to all posts at this level and executes a common lambda on
     * the new specific post data (for every existing post, and newly created ones)
     * WARNING: This function automatically triggers at first (which means that action will be called
     * on every post that falls in the scope of listenToAll -- all the categories / posts / replies
     * below it in the hierarchy)
     * @param action The action taken when a post change/add occurs
     */
    fun listenToAll(action : (ForumPostData) -> Unit)


    /**
     * Sends a notification when there's a new post at this level or below. Upon clicking this
     * notification, the intent will be triggered
     * @param ctx The context of the app
     * @param activityName The activity to launch
     */
    fun sendIntentNotificationOnNewPosts(
        ctx : Context, activityName : Class<*>?
    ) {
        NotificationService.createNotificationChannel(SignInActivity.globalContext)
        val enabledCategoriesNotifications = Storages.storageOf(Storages.FORUM_THEMES_NOTIFICATIONS)

        listenToAll { postData ->
            // Only display notifications of posts written by other users

            val medicalType = enabledCategoriesNotifications.getObjectOrDefault(
                THEME_KEY, MedicalType::class.java, null
            )
            medicalType?.let {
                // Display the notification if the user has enabled this category's notifications
                // and the post hadn't been posted by him and the post is actually a post and not a reply
                if (medicalType.hasCategory(postData.category) && postData.author != getName() && postData.isPost ) {
                    val description = postData.content
                    val title = "New post in ${postData.category} from: ${postData.author}"
                    val intent = Intent(
                        ctx, activityName
                    ).apply {
                        putExtra(EXTRA_FORUM_CATEGORY, postData.category.name)
                    }
                    sendIntentNotification(ctx, title, description, intent)
                }

            }
        }
    }
    /**
     * Sends a notification when a new post happens on this forum
     */

    //--------------------------//
    //-- Navigation functions --//
    //--------------------------//

    /**
     * Returns the root of the forum
     * @return The forum root
     */
    fun root() : Forum

    /**
     * Returns the child forum at the relative path from here
     * If it does not yet exist, it will be created automatically when
     * a first post is sent
     * @param relativePath The relative path to the child forum
     * @return The child forum
     */
    fun child(relativePath : Path) : Forum

    /**
     * Overrides child with a single child instead of an entire relative path
     * Since this is the most common use-case of child, this makes the calls cleaner
     * @param child The name of the child forum
     * @return The child forum
     */
    fun child(child : String) : Forum {
        return child(listOf(child))
    }

    /**
     * Returns the parent forum from the current forum
     * If the current forum is root, it doesn't have a parent and will thus just return itself
     * Category -> root
     * Post -> corresponding category
     * @return The parent forum
     */
    fun parent() : Forum

    companion object {
        private const val MAX_NOTIFICATION_LENGTH = 60
    }
}