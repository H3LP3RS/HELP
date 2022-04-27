package com.github.h3lp3rs.h3lp.messaging

import java.io.Serializable

/**
 * Simple enum representing which role the current user holds in a conversation. It implements the
 * interface Serializable in order to be able to pass this as an extra from an activity to another.
 * HELPER: the current user has accepted to go help someone in need of help
 * HELPEE: the person in need of help
 */
enum class Messenger : Serializable {
    HELPER, HELPEE
}