package com.github.h3lp3rs.h3lp.dataclasses

/**
 * A model describing a rating
 */
data class Rating(
    // Number of stars selected
    val value: Float,
    // Feedback text
    val comment: String
)
