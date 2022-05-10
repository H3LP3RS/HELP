package com.github.h3lp3rs.h3lp.messaging

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.MasterKey
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.MESSAGES
import java.security.*
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import kotlin.text.Charsets.UTF_8

/**
 * Object representing a conversation.
 * @param conversationId The unique conversation id of the conversation, shared by both parties of
 * the conversation, it is the place this conversation is stored in the database
 * @param currentMessenger The user that launched the conversation, used to differentiate between
 *  the user that launched the chat and the other user, for example to display the messages with
 *  matching Messenger as sent by the current user
 */
class Conversation(
    private val conversationId: String,
    private val currentMessenger: Messenger,
    private val context: Context
) {
    private val database = databaseOf(MESSAGES)
    private var publicKey: PublicKey? = null
    private val allMessages: HashMap<String, String> = HashMap()
    private val keyAlias = "CONVERSATION_KEY_$conversationId"

    init {
        //TODO
        // Generate key pair
        // share public key on DB

        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            ANDROID_KEY_STORE
        )

        val keyAlias = "CONVERSATION_KEY_$conversationId"
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
         //   .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()


        kpg.initialize(parameterSpec)

        val kp = kpg.generateKeyPair()

        // send public key to the database
        val encodedPublicKey = Base64.encodeToString(kp.public.encoded, Base64.DEFAULT)
        database.setString(conversationId + "/KEYS/" + currentMessenger.name, encodedPublicKey)

        /*
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, kp.private)

        val iv = cipher.iv

        val encryption = cipher.doFinal("text".toByteArray(UTF_8))

        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        val entry = keyStore.getEntry(keyAlias, null)
        val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val publicKey = keyStore.getCertificate(keyAlias).publicKey

        val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey, spec)

        val plainTextBytes = decryptCipher.doFinal(/* Byte array */)

        val plainText = String(plainTextBytes, UTF_8)
         */
    }

    /**
     * Sends a message from the current user to the database
     * @param messageText The message text
     */
    fun sendMessage(messageText: String) {
        // retrieve public key from Bob
        if (publicKey == null) {
            val path = conversationId + "/KEYS/" + Messenger.values()[(currentMessenger.ordinal + 1) % 2].name
            database.getString(path).thenApply {
                val decodedKey = Base64.decode(it, Base64.DEFAULT)
                val keySpecs = X509EncodedKeySpec(decodedKey)
                publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpecs)
                sendEncryptedMessage(publicKey!!, messageText) //TODO CHANGE THE !!
            }
        } else {
            sendEncryptedMessage(publicKey!!, messageText)
        }
    }

    /**
     *
     */
    private fun sendEncryptedMessage(publicKey: PublicKey, message: String) {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val iv = cipher.iv

        val encryption = cipher.doFinal(message.toByteArray(UTF_8))
        val encryptedMessage = Message(currentMessenger, String(encryption), ""/*iv.toString(UTF_8)*/)

        allMessages[encryptedMessage.message] = message
        database.addToObjectsListConcurrently("$conversationId/MSG/", Message::class.java, encryptedMessage)
    }
    /**
     * Adds a listener on the conversation, the listener is triggered every time a new message is sent to the
     * conversation
     * @param onNewMessage Callback called on every new message
     */
    fun addListener(onNewMessage: (messages: List<Message>, currentMessenger: Messenger) -> Unit) {
        database.addListListener("$conversationId/MSG/", Message::class.java) {
            val list = it.toList().map{ message ->
                retrieveDecryptedMessage(message)
            }
            onNewMessage(it.toList(), currentMessenger)
        }

    }

    fun retrieveDecryptedMessage(encryptedMessage: Message): Message{
        val decryptedMessage = allMessages.getOrDefault(encryptedMessage.message, decryptMessage(encryptedMessage))
        return Message(encryptedMessage.messenger, decryptedMessage as String, encryptedMessage.iv)
    }

    private fun decryptMessage(encryptedMessage: Message): String{
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        val entry = keyStore.getEntry(keyAlias, null)
        val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val publicKey = keyStore.getCertificate(keyAlias).publicKey

        val decryptCipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
        val spec = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT)
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey, spec)

        val plainTextBytes =
            decryptCipher.doFinal(encryptedMessage.message.toByteArray(UTF_8))

        return String(plainTextBytes, UTF_8)
    }

    /**
     * Deletes the conversation from the database
     */
    fun deleteConversation() {
        database.delete(conversationId)
    }

    companion object {
        // Key where we store and get the latest unique conversation id in the database, this allows
        // for concurrent accesses to always get a new id
        const val UNIQUE_CONVERSATION_ID = "unique conversation id"
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}