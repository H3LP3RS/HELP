package com.github.h3lp3rs.h3lp.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.StringBuilder
import kotlin.math.roundToInt

object GDurationJSONParser : JSONParserInterface<String> {
    private const val MIN_IN_SEC = 60
    private const val HOUR_IN_SEC = 60 * MIN_IN_SEC
    private const val DAY_IN_SEC = 24 * HOUR_IN_SEC

    private const val MIN_STR = " min"
    private const val HOUR_STR = " hour"
    private const val DAY_STR = " day"

    /**
     * Function that parses the JSON object to find the searched attribute, transforms it in the
     * correct format and returns it
     * @param obj: the original JSON object
     * @return the transformed object
     */
    override fun parseResult(obj: JSONObject): String {
        return try {
            val legs = parseLegs(obj)
            getDurationFromLegs(legs)
        } catch (e: JSONException) {
            ""
        }
    }


    /**
     * Gets the steps in the path returned from a directions API request
     * @param obj: JSON object returned by a Google directions query
     * @return a JSON array containing all the steps in the path
     */
    private fun parseLegs(obj: JSONObject): JSONArray {
        return obj.getJSONArray("routes").getJSONObject(0)
            .getJSONArray("legs")
    }


    /**
     * Returns the path returned from a directions API request
     * @param steps: JSON array contained in the API response
     * @return the encoded path (which consists of several smaller paths, each in a different
     * step of the API response)
     */
    private fun getDurationFromLegs(legs: JSONArray): String {
        // The directions api unfortunately only returns a series of time estimates for all the
        // legs of a path, not a total, so we add them up

        // totalDuration represents the total duration of the path in seconds
        var totalDuration = 0

        // The path is divided up into a series of steps, we get the corresponding polyline for
        // every step
        for (i in 0 until legs.length()) {
            totalDuration += (legs.getJSONObject(i).getJSONObject("duration").getInt("value"))
        }
        return parseTime(totalDuration)
    }

    private fun parseTime(totalDuration: Int): String {
        var remainingTime = totalDuration
        val days = remainingTime / DAY_IN_SEC
        remainingTime -= days * DAY_IN_SEC
        val hours = remainingTime / HOUR_IN_SEC
        remainingTime -= hours * HOUR_IN_SEC
        // For the minutes, we compute an average to get a more
        // accurate value than a simple floor
        val minutes = (remainingTime / MIN_IN_SEC.toDouble()).roundToInt()

        val stringBuilder = StringBuilder() // TODO : refactor
        if (days != 0) {
            stringBuilder.append(days)
            stringBuilder.append(DAY_STR)
            if (days > 1) {
                stringBuilder.append("s")
            }
            stringBuilder.append(" ")
        }

        if (hours != 0) {
            stringBuilder.append(hours)
            stringBuilder.append(HOUR_STR)
            if (hours > 1) {
                stringBuilder.append("s")
            }
            stringBuilder.append(" ")
        }


        if (minutes != 0) {
            stringBuilder.append(minutes)
            stringBuilder.append(MIN_STR)
            if (minutes > 1) {
                stringBuilder.append("s")
            }
            stringBuilder.append(" ")
        }
        return stringBuilder.toString()
    }
}