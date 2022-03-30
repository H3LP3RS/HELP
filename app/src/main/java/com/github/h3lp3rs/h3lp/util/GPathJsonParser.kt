package com.github.h3lp3rs.h3lp.util

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import org.json.JSONException
import org.json.JSONObject

class GPathJsonParser {

    /**
     * Parses the json of a google place search into a list of places
     * @param obj : Json object returned by a Google place query
     * @return a list of places in the form of maps, with {lat,lng,name} as keys
     */
    fun parseResult(obj: JSONObject): List<LatLng> {
        return try {
            val steps = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps")
            var points: MutableList<LatLng> = mutableListOf()
            for (i in 0 until steps.length()) {
                points.addAll(parsePolyline(steps.getJSONObject(i).getJSONObject("polyline")))
            }
            return points
        } catch (e: JSONException) {
            arrayListOf()
        }

    }

    private fun parsePolyline(overviewPolyline: JSONObject): List<LatLng> {
        val points = overviewPolyline.getString("points")
        Log.i("GPathPolyPoints", points.toString())
        Log.i("GPathPolyPointsRes", PolyUtil.decode(points).toString())
        return PolyUtil.decode(points)
    }
}