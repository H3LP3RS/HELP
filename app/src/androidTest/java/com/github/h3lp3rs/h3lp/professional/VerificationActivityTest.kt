package com.github.h3lp3rs.h3lp.professional

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.professional.CloudStorage
import com.github.h3lp3rs.h3lp.view.professional.ProMainActivity
import com.github.h3lp3rs.h3lp.view.professional.VerificationActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

@RunWith(AndroidJUnit4::class)
class VerificationActivityTest : H3lpAppTest<VerificationActivity>() {

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
        onView(withId(R.id.button_choose_img)).check(
            matches(
                isDisplayed()
            )
        ).perform(ViewActions.click())
        intended(hasType(IMAGE_INTENT))
        intended(hasAction(Intent.ACTION_GET_CONTENT))
    }

    @Test
    fun uploadImgButtonWorks() {
        VerificationActivity.imgUri = TEST_URI
        VerificationActivity.currentUserId = TEST_STRING
        VerificationActivity.currentUserName = TEST_STRING
        Databases.setDatabase(Databases.PRO_USERS, MockDatabase())

        // Mock the Firebase cloud storage
        val storageMock = Mockito.mock(StorageReference::class.java)
        CloudStorage.set(storageMock)

        // Mock the uploading task
        val task = Mockito.mock(UploadTask::class.java)

        doAnswer {
            val successListener: OnSuccessListener<UploadTask.TaskSnapshot> = it.getArgument(0)
            successListener.onSuccess(Mockito.mock(UploadTask.TaskSnapshot::class.java))
            task
        }.`when`(task).addOnSuccessListener(any())

        Mockito.`when`(task.addOnProgressListener(any())).thenReturn(task)
        Mockito.`when`(task.addOnFailureListener(any())).thenReturn(task)

        Mockito.`when`(storageMock.putFile(any())).thenReturn(task)
        Mockito.`when`(storageMock.child(any())).thenReturn(storageMock)

        onView(withId(R.id.button_upload)).check(
            matches(
                isDisplayed()
            )
        ).perform(ViewActions.click())

        intended(IntentMatchers.hasComponent(ProMainActivity::class.java.name))
    }

}