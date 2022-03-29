package com.github.h3lp3rs.h3lp.util

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
            val overviewPolyline = obj.getJSONObject("overview_polyline")
            parsePolyline(overviewPolyline)
        } catch (e: JSONException) {
            arrayListOf()
        }

    }

    private fun parsePolyline(overviewPolyline: JSONObject): List<LatLng> {
        val points = overviewPolyline.getString("points")
        return PolyUtil.decode(points)
    }
}