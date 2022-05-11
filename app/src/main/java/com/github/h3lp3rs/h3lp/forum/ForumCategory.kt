package com.github.h3lp3rs.h3lp.forum

import FireForum

/**
 * Enumeration of the standard, pre-defined forum main categories (root level)
 */
enum class ForumCategory {
    GENERAL, CARDIOLOGY, TRAUMATOLOGY, PEDIATRY, NEUROLOGY, GYNECOLOGY;

    // Map linking the string to the enum value
    val categoriesMap = ForumCategory.values().map { it.name to it }

    private var forum: Forum? = null // Var to enable test-time mocking

    companion object {
        /**
         * Returns one of the main pre-defined categories sub-forum
         * @param category One of the pre-defined main categories
         * @return forum The forum of the given category
         */
        fun forumOf(choice: ForumCategory): Forum {
            // The categories are the first sub-level of the forum, thus they lie after root (the
            // empty list)
            choice.forum = choice.forum ?: FireForum(listOf(choice.name))
            return choice.forum!!
        }
    }
}