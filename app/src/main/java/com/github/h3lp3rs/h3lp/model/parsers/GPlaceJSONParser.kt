package com.github.h3lp3rs.h3lp.parsers

import com.github.h3lp3rs.h3lp.GooglePlace
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 *  This object is used to parse json objects returned by the Google Places API
 *  into Places. Places are represented by Maps with the keys {lat, lng, name}
 */
object GPlaceJSONParser : JSONParserInterface<List<GooglePlace>> {

    /**
     * Parses the json of a google place search into a list of places
     * @param obj : Json object returned by a Google place query
     * @return a list of places in the form of maps, with {lat,lng,name} as keys
     */
    override fun parseResult(obj: JSONObject): List<GooglePlace>? {
        return try {
            val jsonArray = obj.getJSONArray("results")
            parseJSONArray(jsonArray)
        } catch (e: JSONException) {
            null
        }

    }

    /**
     * Parses a JSON array into a list of GooglePlace by parsing each of its elements
     * @param jsonArray The JSON array to parse
     * @return The corresponding list of GooglePlace
     * @throws JSONException in case of a parsing error (the exception is handled in parseResult)
     */
    private fun parseJSONArray(jsonArray: JSONArray): List<GooglePlace> {
        val dataList = ArrayList<GooglePlace>()

        for (i in 0 until jsonArray.length()) {
            val dataMap = parseJSONObject(jsonArray.get(i) as JSONObject)
            dataList.add(dataMap)
        }

        return dataList
    }

    /**
     * Parses a JSON object into a GooglePlace
     * @param obj The JSON object
     * @return Its corresponding GooglePlace
     */
    private fun parseJSONObject(obj: JSONObject): GooglePlace {
        val dataList = HashMap<String, String>()

        // Using the structure of the JSON object to get the name, longitude and latitude of the
        // GooglePlace
        val name = obj.getString("name")
        val location = obj.getJSONObject("geometry")
            .getJSONObject("location")

        val latitude = location.getDouble("lat")

        val longitude = location.getDouble("lng")

        // put the values in the map
        dataList["name"] = name
        dataList["lat"] = latitude.toString()
        dataList["lng"] = longitude.toString()

        return dataList
    }

}
