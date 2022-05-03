package com.github.h3lp3rs.h3lp.forum.data

import java.time.ZonedDateTime

/**
 * Conceptualizes all the data of a simple forum post reply
 * @param author The author of the reply
 * @param content The content of the message (reply)
 * @param postTime The timestamp of the reply
 */
data class ForumPostReplyData(
    val author: String,
    val content: String,
    val postTime: ZonedDateTime
)