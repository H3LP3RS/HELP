package com.github.h3lp3rs.h3lp.util

import org.json.JSONObject

/**
 * General interface representing a JSON parser
 */
interface JSONParserInterface<T> {

    /**
     * Function that parses the JSON object to find the searched attribute, transforms it in the
     * correct format and returns it
     * @param obj: the original JSON object
     * @return the transformed object
     */
    fun parseResult(obj: JSONObject): T
}