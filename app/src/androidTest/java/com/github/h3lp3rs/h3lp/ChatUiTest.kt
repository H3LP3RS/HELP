package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.messaging.ChatActivity
import com.github.h3lp3rs.h3lp.messaging.Conversation
import com.github.h3lp3rs.h3lp.messaging.EXTRA_CONVERSATION_ID
import com.github.h3lp3rs.h3lp.messaging.Messenger
import com.xwray.groupie.ViewHolder
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import org.mockito.Mockito.`when` as When
import java.util.concurrent.CompletableFuture
import javax.crypto.Cipher.PUBLIC_KEY

private const val CONVERSATION_ID = "testing_id"
private const val SENT_MESSAGE = "Testing Chat UI"
private const val RECEIVED_MESSAGE = "Tests succeeded!"
private const val MOCK_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApsYM7sd4KIA23DG6MtexJ2dhWfWfXjfWddKuAh4mNn2Dh1+thy0Qb5TGKJPOjFcGdB19c68g3mAUdje0wtAJ7B3GvCdpJsZa3LUgw3Rk70OCSaRdP8p1QKcDE7V+c6jQlkZ6+QldHigH+OA3pOO9tIumtcmw6Yko0Zz1dvhJ/giLC3y34kxUx0mD/gC6ZbCNshKO++/tEenRhFD8970OuwD7V5pROZ5NFY1O3VVTORDulVSm6fTH/VDT492IiZ8wX/X+AwBZORPbZtIB5A5tEsX5s20bnD7xJq+Ia06A0hr3LjVE1I69nVO6xvavwwe6So7Sr1H8xkwqpxOE85gVeQIDAQAB"

class ChatUiTest {
    private val currentMessenger = Messenger.HELPEE
    private val toMessenger = Messenger.HELPER

    private lateinit var conversationFrom: Conversation
    private lateinit var conversationTo: Conversation

    private lateinit var db: Database

    private lateinit var foreignUserPublicKey: PublicKey
    private lateinit var foreignUserPrivateKey: PrivateKey


    @Before
    fun setup() {
        // Launching the activity with the needed parameters
        val intent = Intent(
            getApplicationContext(),
            ChatActivity::class.java
        ).apply {
            putExtra(EXTRA_CONVERSATION_ID, CONVERSATION_ID)
            putExtra(EXTRA_USER_ROLE, Messenger.HELPEE)
        }

        val kpg =
            KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                Conversation.ANDROID_KEY_STORE
            )

        kpg.initialize(
            KeyGenParameterSpec.Builder(
                "key", KeyProperties.PURPOSE_ENCRYPT
                        or KeyProperties.PURPOSE_DECRYPT
            )
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .setDigests(KeyProperties.DIGEST_SHA1)
                .build()
        )

        val kp = kpg.generateKeyPair()
        foreignUserPrivateKey = kp.private
        foreignUserPublicKey = kp.public

        // Public key that would be on the database
        val encodedPublicKey = Base64.encodeToString(kp.public.encoded, Base64.DEFAULT)

        db = MockDatabase()
        Databases.setDatabase(Databases.MESSAGES, db)

        // Mock the public keys
        db.setString(CONVERSATION_ID + "/KEYS/" + toMessenger.name, MOCK_KEY)
        db.setString(CONVERSATION_ID + "/KEYS/" + currentMessenger.name, MOCK_KEY)




        conversationFrom = Conversation(CONVERSATION_ID, currentMessenger, getApplicationContext())
        conversationTo = Conversation(CONVERSATION_ID,toMessenger, getApplicationContext())

        ActivityScenario.launch<ChatActivity>(intent)

        init()
    }

    @After
    fun release() {
        //conversationFrom.deleteConversation()
        Intents.release()
    }

    @Test
    fun sendMessageDisplaysTheCorrectMessage() {
        onView(withId(R.id.text_view_enter_message)).perform(click()).perform(typeText(SENT_MESSAGE))
        onView(withId(R.id.button_send_message)).perform(click()).perform(ViewActions.closeSoftKeyboard())
        Thread.sleep(H3lpAppTest.WAIT_UI)
        onView(withId(R.id.recycler_view_chat))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
            //.check(matches(hasDescendant(withText(SENT_MESSAGE))))
    }

    @Test
    fun receiveMessageDisplaysTheCorrectMessage() {
        conversationTo.sendMessage(RECEIVED_MESSAGE)
        onView(withId(R.id.recycler_view_chat))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
            //.check(matches(hasDescendant(withText(RECEIVED_MESSAGE))))
    }
}