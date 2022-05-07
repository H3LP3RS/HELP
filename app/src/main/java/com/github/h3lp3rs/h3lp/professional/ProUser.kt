package com.github.h3lp3rs.h3lp.professional

/**
 * Data class representing a professional user
 */
data class ProUser(
    // User's Id
    val id: String,
    // User's name
    val name: String,
    // User's status proof name
    val proofName: String,
    // User's status proof Uri
    val proofUri: String,

    var proStatus: String,

    var proDomain: String,

    var proExperience: String
)