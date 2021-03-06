package com.github.h3lp3rs.h3lp.utils

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.dataclasses.*
import com.github.h3lp3rs.h3lp.model.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.model.dataclasses.BloodType.ABn
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.ADULT_AGE
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.MAX_HEIGHT
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.MAX_WEIGHT
import com.github.h3lp3rs.h3lp.model.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.model.storage.Storages.MEDICAL_INFO
import com.github.h3lp3rs.h3lp.view.firstaid.EXTRA_FIRST_AID
import com.github.h3lp3rs.h3lp.view.firstaid.GeneralFirstAidActivity
import com.google.android.material.textfield.TextInputLayout
import org.apache.commons.lang3.RandomUtils.nextInt
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.util.*
import java.util.concurrent.CompletableFuture
import org.mockito.Mockito.`when` as When

/**
 * Super class for tests in this app containing useful constants and functions
 * that are common to many tests
 */
open class H3lpAppTest<T : Activity> {
    protected val locationManagerMock: LocationManagerInterface =
        mock(LocationManagerInterface::class.java)
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
     * @param context Calling context to be able to access the local storage
     */
    fun loadValidMedicalDataToStorage(context: Context) {
        storageOf(MEDICAL_INFO, context)
            .setObject(
                context.getString(R.string.medical_info_key),
                MedicalInformation::class.java, VALID_MEDICAL_INFO
            )
    }

    /**
     * Mocking the user's location to a null values
     */
    fun mockEmptyLocation() {
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


    /**
     * Checks if a component is correctly displayed on the view
     *
     * @param id Id of the component
     */
    fun checkIfDisplayed(id: Int) {
        onView(withId(id))
            .check(matches(isDisplayed()))
    }

    /**
     * Launches the activity related to the test, is defined as open to allow for classes
     * to override it (and thus launch their activity with various intents). Isn't abstract
     * since not all android tests that extend H3lpAppTest need a launch method
     * @return An activityScenario that launches the activity
     */
    open fun launch(): ActivityScenario<T> {
        throw NotImplementedError("This launch method is not implemented")
    }


    /**
     * General method to launch an activity, initialise intents, do the given action (which can
     * thus use the intents) and release the intents. This makes the tests much prettier
     * @param action Action to run after the intents are launched
     */
    open fun launchAndDo(action: () -> Unit) {
        launch().use {
            initIntentAndCheckResponse()
            action()
            release()
        }
    }

    /**
     * Custom matcher to test error on TextInputLayout.
     * See : https://stackoverflow.com/questions/38842034/how-to-test-textinputlayout-values-hint-error-etc-using-android-espresso
     */
     val hasInputLayoutError: Matcher<View> = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {}
        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            item.error ?: return false
            return true
        }
    }

    /**
     * Custom matcher to test error message on TextInputLayout.
     * See : https://stackoverflow.com/questions/38842034/how-to-test-textinputlayout-values-hint-error-etc-using-android-espresso
     */
    val hasTextInputLayoutError: (String) -> Matcher<View> = { msg: String ->
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {}
            override fun matchesSafely(item: View?) : Boolean {
                if (item !is TextInputLayout) return false
                val error = item.error ?: return false
                return error.toString() == msg
            }
        }
    }

    companion object {
        val TEST_URI: Uri = Uri.EMPTY

        const val TEST_STRING = ""
        const val IMAGE_INTENT = "image/*"

        val VALID_FORMAT_NUMBERS = arrayOf("0216933000", "216933000", "+41216933000")
        const val VALID_CONTACT_NUMBER = "+41216933000"
        const val USER_TEST_ID = "SECRET_AGENT_007"
        const val USER_TEST_NAME = "SECRET_AGENT"
        const val TEST_EMERGENCY_ID = "1"

        // Walking time from the user to the destination according to the Google directions API
        const val TIME_TO_DESTINATION = "1 hour 19 mins"
        const val WAIT_UI = 500L
        const val TEST_TIMEOUT = 3000L

        const val SWISS_LAT = 46.514
        const val SWISS_LONG = 6.604
        const val MAX_RESPONSE_DISTANCE = 5000.0
        const val SWISS_EMERGENCY_NUMBER = "144"

        const val EPIPEN = "Epipen"

        val EPIPEN_SKILL = HelperSkills(
            true, false, false, false, false, false
        )

        val EPIPEN_EMERGENCY_INFO = EmergencyInformation(
            TEST_EMERGENCY_ID, 1.0, 1.0, EPIPEN_SKILL,
            ArrayList(listOf(EPIPEN)), Date(), null, ArrayList()
        )

        val VALID_MEDICAL_INFO = MedicalInformation(
            MAX_HEIGHT - 1,
            MAX_WEIGHT - 1,
            Gender.values()[nextInt() % Gender.values().size], // No gender is more valid than the other
            // Make it so that the user is an adult
            Calendar.getInstance().get(Calendar.YEAR) - ADULT_AGE,
            "",
            "",
            "",
            ABn,
            "",
            VALID_CONTACT_NUMBER
        )
    }
}