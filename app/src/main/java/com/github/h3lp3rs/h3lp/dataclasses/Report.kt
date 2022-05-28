package com.github.h3lp3rs.h3lp.dataclasses

/**
 * A model describing a report
 */
data class Report(
    // Category of the report: bug or suggestion
    val category: String,
    // Description of the report
    val content: String
)
