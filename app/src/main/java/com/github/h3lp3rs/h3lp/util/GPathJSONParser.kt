package com.github.h3lp3rs.h3lp.util

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 *  This class is used to parse json objects returned by the Google Directions API
 *  into polylines (a list of coordinates)
 */
object GPathJSONParser: JSONParserInterface<List<LatLng>> {

    /**
     * Parses the json of a google direction search into a list of coordinates corresponding to
     * a path
     * @param obj: Json object returned by a Google directions query
     * @return a list of coordinates that form a polyline (a path)
     */
    override fun parseResult(obj: JSONObject): List<LatLng> {
        return try {
            val steps = parseSteps(obj)
            return getPathFromSteps(steps)
        } catch (e: JSONException) {
            arrayListOf()
        }

    }

    /**
     * Gets the steps in the path returned from a directions API request
     * @param obj: JSON object returned by a Google directions query
     * @return a JSON array containing all the steps in the path
     */
    private fun parseSteps(obj: JSONObject): JSONArray {
        return obj.getJSONArray("routes").getJSONObject(0)
            .getJSONArray("legs").getJSONObject(0)
            .getJSONArray("steps")
    }

    /**
     * Returns the path returned from a directions API request
     * @param steps: JSON array contained in the API response
     * @return the encoded path (which consists of several smaller paths, each in a different
     * step of the API response)
     */
    private fun getPathFromSteps(steps: JSONArray): List<LatLng> {
        var points: MutableList<LatLng> = mutableListOf()

        // The path is divided up into a series of steps, we get the corresponding polyline for
        // every step
        for (i in 0 until steps.length()) {
            points.addAll(parsePolyline(steps.getJSONObject(i).getJSONObject("polyline")))
        }
        return points
    }

    /**
     * Returns the path from an encoded polyline
     * @param overviewPolyline: JSON object containing the encoded polyline
     * @return the path
     */
    private fun parsePolyline(overviewPolyline: JSONObject): List<LatLng> {
        val points = overviewPolyline.getString("points")
        // The Google directions API returns an encoded path, we have to decode it to retrieve
        // the list of coordinates
        return PolyUtil.decode(points)
    }
}