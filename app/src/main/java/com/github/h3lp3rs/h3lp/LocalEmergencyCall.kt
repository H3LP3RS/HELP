package com.github.h3lp3rs.h3lp

import android.content.Context
import android.location.Geocoder
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.InputStreamReader


class LocalEmergencyCall(var longitude: Double, var latitude: Double, val context: Context) {

    fun getLocalEmergencyNumber(): String {
        return getPhoneNumber(getUserCountry())
    }

    fun getUserCountry(): String? {
        // A geocoder transforms street addresses into coordinates and vice-versa
        val geocoder = Geocoder(context)

        // Trying to get a (partial) address from the user's current location
        val addresses =
            geocoder.getFromLocation(longitude, latitude, MAX_RESULTS) //TODO: is deprecated
        if (addresses.size > 0) {
            return addresses[0].countryName
        }
        // In case the reverse geocoding failed
        return null
    }

    fun getPhoneNumber(searchedCountry: String?): String {
        // In case of a non-valid country, we always return a default emergency number
        if (searchedCountry == null) {
            return DEFAULT_EMERGENCY_NUMBER
        }
        val inputStream = context.resources.openRawResource(R.raw.countries_emergency_numbers)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val csvReader = CSVReader(bufferedReader)

        // Skips the first line (the header) of the CSV file
        csvReader.skip(1)

        var row: Array<String>? = csvReader.readNext()
        while (row != null) {
            val currCountry = row[0]
            val currNumber = row[1]
            if (countryMatches(searchedCountry, currCountry)) {
                return currNumber
            }
            row = csvReader.readNext()
        }

        // If no matching country has been found, return the default value
        return DEFAULT_EMERGENCY_NUMBER
    }

    private fun countryMatches(actual: String, candidate: String): Boolean {
        return candidate.lowercase() == actual.lowercase()
    }



    companion object {
        /**
         * Maximum results returned by the geocoder, kept at 1 since we are only searching
         * for the user's current country and one result is
         */
        private const val MAX_RESULTS = 1

        /**
         * Since 911 is the most common emergency number, if a query of the country fails,
         * we return this as a default value
         */
        const val DEFAULT_EMERGENCY_NUMBER = "911"
    }
}