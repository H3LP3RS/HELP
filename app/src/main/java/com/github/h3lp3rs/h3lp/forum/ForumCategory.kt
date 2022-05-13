package com.github.h3lp3rs.h3lp.forum


/**
 * Enumeration of the standard, pre-defined forum main categories (root level)
 */
enum class ForumCategory {
    GENERAL, CARDIOLOGY, TRAUMATOLOGY, PEDIATRY, NEUROLOGY, GYNECOLOGY;


    private var forum: Forum? = null // Var to enable test-time mocking

    companion object {

        // Map linking the string to the enum value
        val categoriesMap = values().associateBy({it.name}, {it})

        /**
         * Returns one of the main pre-defined categories sub-forum
         * @param choice One of the pre-defined main categories
         * @return forum The forum of the given category
         */
        fun forumOf(choice: ForumCategory): Forum {
            // The categories are the first sub-level of the forum, thus they lie after root (the
            // empty list)
            choice.forum = choice.forum ?: FireForum(listOf(choice.name))
            return choice.forum!!
        }

        /**
         * Used for testing purposes to give forum instances
         * @param newForum The forum to use
         */
        fun setForum(choice: ForumCategory, newForum: Forum) {
            choice.forum = newForum
        }
    }
}