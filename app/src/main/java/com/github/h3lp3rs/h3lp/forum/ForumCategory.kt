package com.github.h3lp3rs.h3lp.forum

import com.github.h3lp3rs.h3lp.database.FireDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.forum.implementation.FireDBForum
import com.github.h3lp3rs.h3lp.forum.implementation.MockDBForum

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
         * @return forum The forum of the given category
         */
        fun forumOf(choice: ForumCategory): Forum {
            // The categories are the first sub-level of the forum, thus they lie after root (the
            // empty list)
            root?.let {
                return it.child(choice.name)
            }
            root = FireDBForum(emptyList(), FireDatabase(ROOT_FORUM_DB_PATH))
            return root!!.child(choice.name)
        }

        /**
         * Returns the root of the forum
         */
        fun root(): Forum {
            root?.let {
                return it
            }
            root = FireDBForum(emptyList(), FireDatabase(ROOT_FORUM_DB_PATH))
            return root!!
        }

        /**
         * Used for testing purposes to activate mocking of the forum
         */
        fun mockForum() {
            root = MockDBForum(emptyList(), MockDatabase())
        }

        // The default category for a forum post
        val DEFAULT_CATEGORY = GENERAL
    }
}