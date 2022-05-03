package com.github.h3lp3rs.h3lp.forum.data

import java.time.ZonedDateTime

/**
 * Conceptualizes all the data of a simple forum post
 * @param author The author of the post
 * @param content The content of the initial message (ie. question)
 * @param postTime The timestamp of the post
 * @param replies The list of all the replies in chronological order
 */
data class ForumPostData(
    val author: String,
    val content: String,
    val postTime: ZonedDateTime,
    val replies: List<ForumPostReplyData>
)