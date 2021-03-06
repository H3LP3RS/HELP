package com.github.h3lp3rs.h3lp.model.messaging

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.KEY_ALGORITHM_RSA
import android.util.Base64
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.MESSAGES
import com.github.h3lp3rs.h3lp.model.storage.Storages
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Object representing a conversation.
 * @param conversationId The unique conversation id of the conversation, shared by both parties of
 * the conversation, it is the place this conversation is stored in the database
 * @param currentMessenger The user that launched the conversation, used to differentiate between
 *  the user that launched the chat and the other user, for example to display the messages with
 *  matching Messenger as sent by the current user
 * @param context The context of the activity that launched the Conversation (to access the local
 * storages)
 */
class Conversation(
    val conversationId: String,
    private val currentMessenger: Messenger,
    private val context: Context
) {
    private val database = databaseOf(MESSAGES, context)
    private var publicKey: PublicKey? = null
    private val allMessages: HashMap<String, String> = HashMap()


    /**
     * Sends a message from the current user to the database
     * @param messageText The message text
     */
    fun sendMessage(messageText: String) {
        // Retrieve public key from Bob

        if (publicKey == null) {
            val messengerName = Messenger.values()[(currentMessenger.ordinal + 1) % 2].name

            database.getString(publicKeyPath(conversationId, messengerName)).thenApply {
                val decodedKey = Base64.decode(it, Base64.DEFAULT)
                val keySpecs = X509EncodedKeySpec(decodedKey)
                publicKey = KeyFactory.getInstance(KEY_ALGORITHM_RSA).generatePublic(keySpecs)
                encryptAndSend(publicKey!!, messageText)
            }
        } else {
            encryptAndSend(publicKey!!, messageText)
        }
    }

    /**
     * Encrypts the given message with the given public key and sends it to the
     * database
     * @param publicKey The public key to be used for encryption
     * @param message The message to encrypt
     */
    private fun encryptAndSend(publicKey: PublicKey, message: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val bytes = cipher.doFinal(message.toByteArray())

        val encryptedMessage =
            Message(currentMessenger, Base64.encodeToString(bytes, Base64.DEFAULT))

        allMessages[encryptedMessage.message] = message
        database.addToObjectsListConcurrently(
            "$conversationId/MSG/",
            Message::class.java,
            encryptedMessage
        )
    }

    /**
     * Adds a listener on the conversation, the listener is triggered every time a new message is sent to the
     * conversation. Acts on the list of all decrypted messages.
     * @param onNewMessage Callback called on every new message
     */
    fun addListListener(onNewMessage: (messages: List<Message>, currentMessenger: Messenger) -> Unit) {
        database.addListListener("$conversationId/MSG/", Message::class.java) {
            it.map { message ->
                retrieveDecryptedMessage(message)
            }
            onNewMessage(it, currentMessenger)
        }
    }

    /**
     * Adds a listener on the conversation, the listener is triggered every time a new message is sent to the
     * conversation. act on the new message
     * @param onNewMessage Callback called on every new message
     */
    fun newMessageListener(onNewMessage: (messages: Message) -> Unit) {
        database.addEventListener("$conversationId/MSG/",
            Message::class.java,
            { msg -> run { onNewMessage(retrieveDecryptedMessage(msg)) } },
            {})

    }

    /**
     * Adds a listener on the conversation, the listener is triggered when the conversation is deleted
     * @param onDeletedConversation Callback called when the conversation deleted
     */
    fun deleteConversationListener(onDeletedConversation: (key: String) -> Unit) {
        database.addEventListener(null, String::class.java, null) { key ->
            run {
                // If the key is the conversation Id, that means that the user deleted the current
                // conversation
                if (key == conversationId) {
                    onDeletedConversation(
                        key
                    )
                }
            }
        }
    }

    /**
     * Auxiliary function to retrieve a message from its encrypted value
     * @param encryptedMessage the full encrypted message
     * @return the full decrypted message
     */
    private fun retrieveDecryptedMessage(encryptedMessage: Message): Message {
        val decryptedMessage = if (allMessages.containsKey(encryptedMessage.message)) {
            allMessages[encryptedMessage.message]!!
        } else {
            decryptMessage(encryptedMessage)
        }

        return Message(encryptedMessage.messenger, decryptedMessage)
    }

    /**
     * Auxiliary function to decrypt a message using the user's private key
     * @param encryptedMessage message to decrypt
     * @return the decrypted message's content
     */
    private fun decryptMessage(encryptedMessage: Message): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        val privateKey =
            keyStore.getKey(keyAlias(conversationId, currentMessenger.name), null) as PrivateKey?

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val encryptedData = Base64.decode(encryptedMessage.message, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    /**
     * Deletes the conversation from the database
     */
    fun deleteConversation() {
        database.delete(conversationId)
    }


    /**
     * Load the cache if it exists
     * @warning should be called in onCreate method of the activity
     */
    fun loadChatCache() {
        val storage = storageOf(Storages.MSG_CACHE, context)

        val cache: HashMap<String, String> = storage.getObjectOrDefault(
            conversationId,
            HashMap::class.java,
            HashMap<String, String>()
        ) as HashMap<String, String>

        allMessages.putAll(cache)
    }


    /**
     * Save the cache for the conversation
     * @warning should be called in the onPause method of the activity
     */
    fun saveChatCache() {
        Storages.MSG_CACHE.setOnlineSync(false, context)
        val storage = storageOf(Storages.MSG_CACHE, context)

        storage.setObject(conversationId, HashMap::class.java, allMessages)
    }

    companion object {
        // Key where we store and get the latest unique conversation id in the database, this allows
        // for concurrent accesses to always get a new id
        const val UNIQUE_CONVERSATION_ID = "unique conversation id"
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEYS_SUB_PATH = "KEYS"
        private const val TRANSFORMATION = "RSA/ECB/OAEPwithSHA-1andMGF1Padding"

        /**
         * Auxiliary function to get the key alias.
         * @param conversationId conversationId corresponding to the conversation
         * we want the key for
         * @param messengerName name of the current messenger
         * @return the corresponding key alias
         */
        fun keyAlias(conversationId: String, messengerName: String): String {
            return "CONVERSATION_KEY_${conversationId}_${messengerName}"
        }

        /**
         * Auxiliary function to get the path of the public key.
         * @param conversationId Id of the conversation
         * @param messengerName name of the messenger whose key we want
         */
        fun publicKeyPath(conversationId: String, messengerName: String): String {
            return "${conversationId}/$KEYS_SUB_PATH/${messengerName}"
        }

        /**
         * Creates a key pair for a given conversation. The public key is sent to
         * the database and the private key remains in Android's keystore.
         * @param conversationId Id of the conversation
         * @param messenger Name of the messenger creating the key pair
         * @param context The calling activity's context (to access the messages database)
         */
        fun createAndSendKeyPair(conversationId: String, messenger: Messenger, context: Context) {
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
            val aliases = keyStore.aliases().toList()
            val keyAlias = keyAlias(conversationId, messenger.name)
            val pubKey: PublicKey

            // Create the key pair if it does not exist already
            if (!aliases.contains(keyAlias)) {
                val kpg =
                    KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, ANDROID_KEY_STORE)

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
                pubKey = kp.public
            } else {
                val cert = keyStore.getCertificate(keyAlias)
                pubKey = cert.publicKey
            }

            // Send public key to the database
            val encodedPublicKey = Base64.encodeToString(pubKey.encoded, Base64.DEFAULT)
            databaseOf(MESSAGES, context).setString(
                publicKeyPath(conversationId, messenger.name),
                encodedPublicKey
            )
        }
    }
}