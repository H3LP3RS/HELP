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
    val conversationId: String,
    private val currentMessenger: Messenger
) {
    private val database = databaseOf(MESSAGES)
    private var publicKey: PublicKey? = null
    private val allMessages: HashMap<String, String> = HashMap()
    private val keyAlias = "CONVERSATION_KEY_$conversationId"
    private val TRANSFORMATION = "RSA/ECB/OAEPwithSHA-1andMGF1Padding"
    init {
        //TODO
        // Generate key pair
        // share public key on DB


        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        val alias = keyStore.aliases().toList()

        if(!alias.contains(keyAlias)) {
            val kpg =
                KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE)

            kpg.initialize(
                KeyGenParameterSpec.Builder(
                    keyAlias, KeyProperties.PURPOSE_ENCRYPT
                            or KeyProperties.PURPOSE_DECRYPT
                )
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .setDigests(KeyProperties.DIGEST_SHA1)
                    .build()
            )

            val kp = kpg.generateKeyPair()

            // send public key to the database
            val encodedPublicKey = Base64.encodeToString(kp.public.encoded, Base64.DEFAULT)
            database.setString(conversationId + "/KEYS/" + currentMessenger.name, encodedPublicKey)
        }

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
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val bytes = cipher.doFinal(message.toByteArray())

        val encryptedMessage = Message(currentMessenger, Base64.encodeToString(bytes, Base64.DEFAULT), ""/*iv.toString(UTF_8)*/)

        allMessages[encryptedMessage.message] = message
        database.addToObjectsListConcurrently("$conversationId/MSG/", Message::class.java, encryptedMessage)
    }
    /**
     * Adds a listener on the conversation, the listener is triggered every time a new message is sent to the
     * conversation. act on the list of all messages
     * @param onNewMessage Callback called on every new message
     */
    fun addListListener(onNewMessage: (messages: List<Message>, currentMessenger: Messenger) -> Unit) {
        database.addListListener("$conversationId/MSG/", Message::class.java) {
            val list = it.toList().map{ message ->
                retrieveDecryptedMessage(message)
            }
            onNewMessage(it.toList(), currentMessenger)
        }
    }

    /**
     * Adds a listener on the conversation, the listener is triggered every time a new message is sent to the
     * conversation. act on the new message
     * @param onNewMessage Callback called on every new message
     */
    fun newMessageListener(onNewMessage: (messages : Message) -> Unit){
        database.addEventListener("$conversationId/MSG/",
            Message::class.java,
            {msg -> run {onNewMessage(retrieveDecryptedMessage(msg))}},
            {})

    }

    /**
     * Adds a listener on the conversation, the listener is triggered every time a new message is sent to the
     * conversation. act on the new message
     * @param onNewMessage Callback called on every new message
     */
    fun deleteConversationListener(onDeletedConversation: (key : String) -> Unit){
        database.addEventListener(null, String::class.java, null) { key ->
            run {
                if (key == conversationId) {
                    onDeletedConversation(
                        key
                    )
                }
            }
        }
    }

    fun retrieveDecryptedMessage(encryptedMessage: Message): Message{
        val decryptedMessage = if (allMessages.containsKey(encryptedMessage.message)) {
            allMessages[encryptedMessage.message]!!
        } else{
            decryptMessage(encryptedMessage)
        }

        return Message(encryptedMessage.messenger, decryptedMessage as String, encryptedMessage.iv)
    }

    private fun decryptMessage(encryptedMessage: Message): String{
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        val privateKey = keyStore.getKey(keyAlias, null) as PrivateKey?

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE,privateKey)
        val encryptedData = Base64.decode(encryptedMessage.message,Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
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