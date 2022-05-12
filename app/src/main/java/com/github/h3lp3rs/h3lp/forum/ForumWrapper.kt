package com.github.h3lp3rs.h3lp.forum

object ForumWrapper {
    // ForumWrapper contains the currently used forum
    private var forum: Forum? = null

    /**
     * Returns the current forum (the default  cloud storage is with FireForum, unless set
     * otherwise)
     * @return The forum
     */
    fun get(category: String): Forum {
        forum = forum ?: ForumCategory.categoriesMap[category]?.let { ForumCategory.forumOf(it) }!!
        return forum!!
    }

    /**
     * Used for testing purposes to give mock forum instances, can also be used to enable
     * multiple forums for the app
     */
    fun set(newForum: Forum) {
        forum = newForum
    }

}