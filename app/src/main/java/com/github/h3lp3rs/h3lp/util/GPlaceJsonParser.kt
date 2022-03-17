package com.github.h3lp3rs.h3lp.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 *  This class is used to parse json objects returned by the Google Places API
 *  into Places. Places are represented by Maps with the keys {lat, lng, name}
 */
class GPlaceJsonParser {


    private fun parseJsonObject(obj: JSONObject): HashMap<String, String> {
        val dataList = HashMap<String, String>()

        val name = obj.getString("name")
        val latitude = obj.getJSONObject("geometry")
            .getJSONObject("location").getDouble("lat")

        val lontitude = obj.getJSONObject("geometry")
            .getJSONObject("location")
            .getDouble("lng")

        // put the values in the map
        dataList["name"] = name
        dataList["lat"] = latitude.toString()
        dataList["lng"] = lontitude.toString()

        return dataList
    }

    private fun parseJsonArray(jsonArray: JSONArray): List<HashMap<String,String>>{
        val dataList = ArrayList<HashMap<String,String>>()

        for (i in 0 until jsonArray.length()) {
            val dataMap = parseJsonObject(jsonArray.get(i) as JSONObject)
            dataList.add(dataMap)
        }

        return dataList
    }

    /**
     * Parses the json of a google place search into a list of places
     * @param obj : Json object returned by a Google place query
     * @return a list of places in the form of maps, with {lat,lng,name} as keys
     */
    fun parseResult(obj: JSONObject): List<HashMap<String, String>> {
        return try {
            val jsonArray = obj.getJSONArray("results")
            parseJsonArray(jsonArray)
        } catch(e: JSONException) {
            arrayListOf()
        }

    }
}
