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

/**
 * Helper class which, given a Google api key, displays several methods used by many activities to
 * use Google APIs, parse the results and display them on any map fragment
 */
class GoogleAPIHelper(private val apiKey: String): CoroutineScope by MainScope() {

    /**
     * Retrieves the shortest walking path to the destination and displays it on the map
     * @param currentLat The user's current latitude
     * @param currentLong The user's current longitude
     * @param destinationLat The destination's latitude
     * @param destinationLong The destination's longitude
     * @param mapsFragment The map fragment to display the path on
     * @param usePathData Method that acts as a future on the String returned by the Google
     *  directions API in case the caller needs to reuse it, the default is doing nothing
     * @param
     */
    fun displayWalkingPath(
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

            // Retrieve the path from the JSON received
            val path = parseTask(data, GPathJSONParser)
            path.let {
                // Displays the path and adds a marker for the end point
                mapsFragment.showPolyline(path)
                mapsFragment.addMarker(
                    destinationLat, destinationLong,
                    END_POINT_NAME
                )
            }
            // Lets the calling method use the returned JSON for additional calls
            usePathData(data)
        }
    }

    /**
     * General method to parse a string with any JSON parser
     */
    fun <T> parseTask(data: String, parser: JSONParserInterface<T>): T {
        return parser.parseResult(JSONObject(data))
    }

    /**
     * General method to download the information returned by accessing an url
     * @param url The url to download from
     */
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
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
        const val DEFAULT_SEARCH_RADIUS = 3000

        // Constants to access the Google directions API
        const val DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json"
        const val WALKING = "walking"

        private const val END_POINT_NAME = "user in need"
    }

}