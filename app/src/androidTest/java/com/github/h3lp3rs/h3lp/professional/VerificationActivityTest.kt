package com.github.h3lp3rs.h3lp.professional

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class VerificationActivityTest {
    private val TEST_URI = Uri.EMPTY

    @get:Rule
    val testRule = ActivityScenarioRule(
        VerificationActivity::class.java
    )

    @Before
    fun setup() {
        init()
    }

    @After
    fun clean() {
        release()
    }

    @Test
    fun chooseImgButtonWorks() {
        Espresso.onView(ViewMatchers.withId(R.id.button_choose_img)).check(
            matches(
                isDisplayed()
            )
        ).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasType("image/*"))
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT))
    }

    /*@Test
    fun uploadImgButtonWorks() {
        VerificationActivity.imgUri = TEST_URI
        Databases.PRO_USERS.db = MockDatabase()
        val storageMock = Mockito.mock(StorageReference::class.java)
        val task =  UploadTask()
        Mockito.`when`(storageMock.putFile(TEST_URI)).thenReturn(Tasks.forResult(suc))
        Espresso.onView(ViewMatchers.withId(R.id.button_upload)).check(
            matches(
                isDisplayed()
            )
        ).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(ProMainActivity::class.java.name))
    }*/

}