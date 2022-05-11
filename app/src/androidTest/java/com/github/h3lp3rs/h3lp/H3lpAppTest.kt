package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.h3lp3rs.h3lp.dataclasses.*
import com.github.h3lp3rs.h3lp.dataclasses.BloodType.ABn
import com.github.h3lp3rs.h3lp.dataclasses.Gender.Female
import com.github.h3lp3rs.h3lp.dataclasses.Gender.Male
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation.Companion.MAX_HEIGHT
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation.Companion.MAX_WEIGHT
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.storage.Storages.MEDICAL_INFO
import org.apache.commons.lang3.RandomUtils.nextBoolean
import org.mockito.Mockito.`when` as When
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Super class for tests in this app containing useful constants and functions
 * that are common to many tests
 */
open class H3lpAppTest {

    private val locationManagerMock: LocationManagerInterface = mock(LocationManagerInterface::class.java)
    private val locationMock: Location = mock(Location::class.java)


    /**
     * Auxiliary function to perform the repetitive step of starting espresso's
     * intent capture
     */
    fun initIntentAndCheckResponse() {
        init()
        val intent = Intent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(IntentMatchers.anyIntent()).respondWith(intentResult)
    }

    /**
     * Auxiliary function to load a valid medical information to the corresponding
     * storage
     */
    fun loadValidMedicalDataToStorage() {
        storageOf(MEDICAL_INFO)
            .setObject(
                SignInActivity.globalContext.getString(R.string.medical_info_key),
                MedicalInformation::class.java, VALID_MEDICAL_INFO)
    }

    /**
     * Mocking the user's location to a null values
     */
    fun mockEmptyLocation(){
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            CompletableFuture.completedFuture(
                locationMock
            )
        )
        GeneralLocationManager.set(locationManagerMock)
    }

    /**
     * Mocking the user's location to a predefined set of coordinates
     */
    fun mockLocationToCoordinates(longitude: Double, latitude: Double) {
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            CompletableFuture.completedFuture(locationMock)
        )
        When(locationMock.longitude).thenReturn(longitude)
        When(locationMock.latitude).thenReturn(latitude)
        GeneralLocationManager.set(locationManagerMock)
    }

    /**
     * Mocking the location manager as if an error occurred (in which case, the returned future
     * fails)
     */
    fun mockFailingLocation() {
        // Mocking the location manager as if an error occurred (in which case, the returned future
        // fails)
        val failingFuture: CompletableFuture<Location> = CompletableFuture()
        failingFuture.completeExceptionally(RuntimeException(LocationManagerInterface.GET_LOCATION_EXCEPTION))
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            failingFuture
        )

        GeneralLocationManager.set(locationManagerMock)
    }

    companion object {
        val TEST_URI: Uri = Uri.EMPTY

        const val TEST_STRING = ""
        const val IMAGE_INTENT = "image/*"

        val VALID_FORMAT_NUMBERS = arrayOf("0216933000", "216933000", "+41216933000")
        const val VALID_CONTACT_NUMBER = "+41216933000"
        const val USER_TEST_ID = "SECRET_AGENT_007"
        const val TEST_EMERGENCY_ID = "1"

        // Walking time from the user to the destination according to the Google directions API
        const val TIME_TO_DESTINATION = "1 hour 19 mins"
        const val WAIT_UI = 500L
        const val TEST_TIMEOUT = 3000L

        const val SWISS_LAT = 46.514
        const val SWISS_LONG = 6.604
        const val SWISS_EMERGENCY_NUMBER = "144"

        const val EPIPEN = "Epipen"

        val EPIPEN_SKILL = HelperSkills(
            true, false,false, false,false, false
        )

        val EPIPEN_EMERGENCY_INFO = EmergencyInformation(
            TEST_EMERGENCY_ID, 1.0,1.0, EPIPEN_SKILL,
            ArrayList(listOf(EPIPEN)), Date(), null, ArrayList())

        val VALID_MEDICAL_INFO = MedicalInformation(
            MAX_HEIGHT -1,
            MAX_WEIGHT -1,
            if(nextBoolean()) Male else Female, // no gender is more valid than the other
            Calendar.getInstance().get(Calendar.YEAR),
            "",
            "",
            "",
            ABn,
            "",
            VALID_CONTACT_NUMBER)
    }
}