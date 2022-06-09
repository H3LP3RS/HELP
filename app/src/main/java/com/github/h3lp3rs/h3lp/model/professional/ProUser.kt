package com.github.h3lp3rs.h3lp.model.professional

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
    // User's professional status ex: Doctor, nurse,,,
    var proStatus: String,
    // User's professional domain ex: neurology...
    var proDomain: String,
    // User's professional experience ex: 5 years
    var proExperience: String
)