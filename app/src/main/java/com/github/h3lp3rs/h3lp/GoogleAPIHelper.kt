package com.github.h3lp3rs.h3lp

import android.util.Log
import com.github.h3lp3rs.h3lp.util.GPathJSONParser
import com.github.h3lp3rs.h3lp.util.JSONParserInterface
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GoogleAPIHelper(private val apiKey: String): CoroutineScope by MainScope() {


    /**
     * Retrieves the shortest path to the destination and displays it on the map
     */
    fun displayPath(
        currentLat: Double,
        currentLong: Double,
        destinationLat: Double,
        destinationLong: Double,
        mapsFragment: MapsFragment,
        usePathData: (String?) -> Unit = {}
    ) {
        val url = DIRECTIONS_URL + "?destination=" + destinationLat + "," + destinationLong +
                "&mode=${WALKING}" +
                "&origin=" + currentLat + "," + currentLong +
                "&key=" + apiKey

        // Launches async routines to retrieve the path to the destination and display it on the map
        CoroutineScope(Dispatchers.Main).launch {
            val data: String = withContext(Dispatchers.IO) { downloadUrl(url) }
            Log.i("GPath", data)
            val path = parseTask(data, GPathJSONParser)
            path.let {
                mapsFragment.showPolyline(path)
                mapsFragment.addMarker(
                    destinationLat, destinationLong,
                    END_POINT_NAME
                )
            }
            usePathData(data)
        }
    }

    fun <T> parseTask(data: String, parser: JSONParserInterface<T>): T {
        return parser.parseResult(JSONObject(data))
    }

    fun downloadUrl(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()

        val stream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(stream))

        val builder = StringBuilder()

        reader.use { r ->
            var line = r.readLine()
            while (line != null) {
                builder.append(line)
                line = r.readLine()
            }
        }

        return builder.toString()
    }

    companion object{
        // Constants to access the Google places API
        const val PLACES_URL =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json" //TODO : make these constants private
        const val DEFAULT_SEARCH_RADIUS = 3000

        // Constants to access the Google directions API

        const val DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json"
        const val WALKING = "walking"

        const val END_POINT_NAME = "user in need"
    }

}