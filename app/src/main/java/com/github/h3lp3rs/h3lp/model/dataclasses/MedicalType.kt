package com.github.h3lp3rs.h3lp.model.dataclasses

import com.github.h3lp3rs.h3lp.model.forum.ForumCategory

class MedicalType(private val categories: List<ForumCategory>) {

    /**
     * Returns the fact that this medical type has the given category
     * @param category The category to check for
     * @return True if this medical type contains this category, false otherwise
     */
    fun hasCategory(category: ForumCategory): Boolean {
        return categories.contains(category)
    }


}