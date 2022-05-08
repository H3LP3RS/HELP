package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.h3lp3rs.h3lp.dataclasses.*
import com.github.h3lp3rs.h3lp.dataclasses.BloodType.ABn
import com.github.h3lp3rs.h3lp.dataclasses.Gender.Female
import com.github.h3lp3rs.h3lp.dataclasses.Gender.Male
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation.Companion.MAX_HEIGHT
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation.Companion.MAX_WEIGHT
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.MEDICAL_INFO
import org.apache.commons.lang3.RandomUtils
import org.apache.commons.lang3.RandomUtils.nextBoolean
import java.util.*

/**
 * Super class for tests in this app containing useful constants and functions
 * that are common to many tests
 */
open class H3lpAppTest {

    /**
     * Auxiliary function to perform the repetitive step of starting espresso's
     * intent capture
     */
    fun initIntentAndCheckResponse() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
    }

    /**
     * Auxiliary function to load a valid medical information to the corresponding
     * storage
     */
    fun loadValidMedicalDataToStorage(){
        Storages.storageOf(MEDICAL_INFO)
            .setObject(
                SignInActivity.globalContext.getString(R.string.medical_info_key),
                MedicalInformation::class.java, VALID_MEDICAL_INFO)
    }

    companion object {
        val TEST_URI: Uri = Uri.EMPTY

        const val TEST_STRING = ""
        const val IMAGE_INTENT = "image/*"

        val VALID_FORMAT_NUMBERS = arrayOf("0216933000", "216933000", "+41216933000")
        const val VALID_CONTACT_NUMBER = "+41216933000"
        const val USER_TEST_ID = "SECRET_AGENT_007"
        const val TEST_EMERGENCY_ID = "1"

        const val WAIT_UI = 500L
        const val TEST_TIMEOUT = 3000L

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