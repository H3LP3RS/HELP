package com.github.h3lp3rs.h3lp.parsers

import org.json.JSONObject

/**
 * General interface representing a JSON parser
 */
interface JSONParserInterface<T> {

    /**
     * Function that parses the JSON object to find the searched attribute, transforms it in the
     * correct format and returns it
     * @param obj The original JSON object
     * @return The transformed object (or null if there was a parsing error)
     */
    fun parseResult(obj: JSONObject): T?
}