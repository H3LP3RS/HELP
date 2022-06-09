package com.github.h3lp3rs.h3lp.model.forum


import android.content.Context
import com.github.h3lp3rs.h3lp.forum.implementation.CachedForum
import com.github.h3lp3rs.h3lp.model.database.FireDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.forum.data.Forum
import com.github.h3lp3rs.h3lp.model.forum.implementation.FireDBForum
import com.github.h3lp3rs.h3lp.model.forum.implementation.MockDBForum

/**
 * Enumeration of the standard, pre-defined forum main categories (root level)
 */
enum class ForumCategory {
    GENERAL, CARDIOLOGY, TRAUMATOLOGY, PEDIATRY, NEUROLOGY, GYNECOLOGY;

    companion object {

        private const val ROOT_FORUM_DB_PATH = "FORUM"

        // Var to enable test-time mocking
        private var root: Forum? = null

        // Map linking the string to the enum value
        val categoriesMap = values().associateBy({ it.name }, { it })

        /**
         * Returns one of the main pre-defined categories sub-forum
         * @param choice One of the pre-defined main categories
         * @param context The calling activity's context as required by FireDatabase
         * @return forum The forum of the given category
         */
        fun forumOf(choice: ForumCategory, context: Context): Forum {
            // The categories are the first sub-level of the forum, thus they lie after root (the
            // empty list)
            return root(context).child(choice.name)
        }

        /**
         * Returns the cached version of forumOf
         * @see forumOf
         */
        fun cachedForumOf(choice: ForumCategory, context: Context): Forum {
            return CachedForum(forumOf(choice))
        }

        /**
         * Returns the root of the forum
         * @param context The calling activity's context as required by FireDatabase
         */
        fun root(context: Context): Forum {
            root?.let {
                return it
            }
            root = FireDBForum(emptyList(), FireDatabase(ROOT_FORUM_DB_PATH, context), context)
            return root!!
        }

        /**
         * Used for testing purposes to activate mocking of the forum
         * @param context The calling activity's context as required by MockDBForum
         */
        fun mockForum(context: Context) {
            root = MockDBForum(emptyList(), MockDatabase(), context)
        }

        // The default category for a forum post
        val DEFAULT_CATEGORY = GENERAL
    }
}