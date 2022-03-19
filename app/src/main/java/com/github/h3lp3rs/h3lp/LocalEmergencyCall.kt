package com.github.h3lp3rs.h3lp

import android.content.Context
import android.location.Criteria
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService

import android.location.Geocoder
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*


class LocalEmergencyCall(var longitude: Double, var latitude: Double, val context: Context) {
//    fun get(): phoneNUmber

    fun getUserCountry(): String? {
        // A geocoder transforms street addresses into coordinates and vice-versa
        val geocoder = Geocoder(context, Locale.getDefault())

        // Trying to get a (partial) address from the user's current location
        val addresses = geocoder.getFromLocation(longitude, latitude, MAX_RESULTS) //TODO: is deprecated
        if (addresses.size > 0) {
            return addresses[0].countryName
        }
        // In case the reverse geocoding failed
        return null
    }

    fun getPhoneNumber(countryName: String): String? {
        val inputStream = context.resources.openRawResource(R.raw.countries_emergency_numbers)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val csvReader = CSVReader(bufferedReader)
        csvReader.iterator().forEach {  row ->
            if (countryMatches(countryName, row.toString())) {
                return row.toString()
            }
        }
        return null
    }

    fun countryMatches(actual: String, candidate: String): Boolean {
        return candidate.lowercase(Locale.getDefault())
            .contains(actual.lowercase(Locale.getDefault()))
    }



    companion object {
        /**
         * Maximum results returned by the geocoder, kept at 1 since we are only searching
         * for the user's current country and one result is
         */
        private const val MAX_RESULTS = 1
    }
    }