package com.github.h3lp3rs.h3lp.model.map

import android.content.Context
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Object that serves to retrieve the coordinates of defibrillators from any csv with defibrillator
 * locations (to streamline the addition of defibrillator locations for other countries)
 * The csv should be of the format:
 * -- Header
 * -- latitude,longitude
 * -- latitude,longitude
 * -- ...
 */
object AedLocationsRetriever {
    fun retrieveFromFile(fileId: Int, context: Context): List<HashMap<String, String>> {
        // Instantiating the defibrillator locations database
        val inputStream = context.resources.openRawResource(fileId)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val csvReader = CSVReader(bufferedReader)

        // Skips the first line (the header) of the CSV file
        csvReader.skip(1)
        var aedLocations: List<HashMap<String, String>> = emptyList()
        var row: Array<String>? = csvReader.readNext()
        while (row != null) {
            // Latitude is stored in the first column, longitude in the second
            val lat = row[0]
            val long = row[1]
            // Also adding a name to each aed (this makes it easier to display them on the map)
            aedLocations = aedLocations + hashMapOf("name" to "aed", "lat" to lat, "lng" to long)
            row = csvReader.readNext()
        }
        csvReader.close()
        return aedLocations
    }
}
