package com.github.h3lp3rs.h3lp.helprequest

import android.content.Context
import android.location.Geocoder
import com.github.h3lp3rs.h3lp.R
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Class containing methods enabling the Call Emergency Services button to make the number called
 * match the emergency services number in the user's current location
 */
object LocalEmergencyCaller {
    /**
     * Maximum results returned by the geocoder, kept at 1 since we are only searching
     * for the user's current country and one returned address is enough for that
     */
    private const val MAX_RESULTS = 1

    /**
     * Since 911 is the most common emergency number, if a query of the country fails,
     * we return this as a default value
     */
    const val DEFAULT_EMERGENCY_NUMBER = "911"

    /**
     * General method to get the emergency number from the country corresponding to a set
     * of coordinates
     *
     * @param longitude User's longitude
     * @param latitude User's lat
     * @param context The calling activity's context (which is required to instantiate a
     * Geocoder and to open the emergency numbers database)
     * @return The corresponding emergency phone number or a default emergency number in case an
     * error occurred
     */
    fun getLocalEmergencyNumber(longitude: Double?, latitude: Double?, context: Context): String {
        return getPhoneNumberFromCountry(context, getUserCountry(longitude, latitude, context))
    }

    /**
     * Method to get the country in which a set of coordinates are located (or null in case
     * these coordinates aren't in a country)
     *
     * @param longitude User's longitude
     * @param latitude User's lat
     * @param context The calling activity's context (which is required to instantiate a Geocoder)
     * The corresponding country
     */
    fun getUserCountry(longitude: Double?, latitude: Double?, context: Context): String? {
        // In case the given coordinates were null, we return as if they didn't correspond
        // to any country
        if (longitude == null || latitude == null) {
            return null
        }
        // A geocoder transforms street addresses into coordinates and vice-versa
        val geocoder = Geocoder(context)

        // Trying to get a (partial) address from the user's current location
        val addresses = geocoder.getFromLocation(latitude, longitude, MAX_RESULTS)
        if (addresses.size > 0) {
            return addresses[0].countryName
        }
        // In case the reverse geocoding failed
        return null
    }

    /**
     * Method that accesses the emergency phone numbers database to retrieve the number
     * associated with a given country
     *
     * @param context The calling activity's context (which is required to open the emergency
     * phone numbers database)
     * @param searchedCountry The country whose emergency phone number we are currently looking
     * for
     * @return The corresponding phone number, or a default value in case the country wasn't
     * valid
     */
    fun getPhoneNumberFromCountry(context: Context, searchedCountry: String?): String {
        // In case of a non-valid country, we always return a default emergency number
        if (searchedCountry == null) {
            return DEFAULT_EMERGENCY_NUMBER
        }
        // Instantiating the emergency phone numbers database
        val inputStream = context.resources.openRawResource(R.raw.countries_emergency_numbers)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val csvReader = CSVReader(bufferedReader)

        // Skips the first line (the header) of the CSV file
        csvReader.skip(1)

        var row: Array<String>? = csvReader.readNext()
        while (row != null) {
            // The country is stored in the first column, the phone number is stored in the
            // second
            val currCountry = row[0]
            val currNumber = row[1]
            if (currCountry.lowercase() == searchedCountry.lowercase()) {
                return currNumber
            }
            row = csvReader.readNext()
        }

        csvReader.close()

        // If no matching country has been found, return the default value
        return DEFAULT_EMERGENCY_NUMBER
    }

}