package com.github.h3lp3rs.h3lp.model.storage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import java.lang.Boolean.parseBoolean

/**
 * Enumeration of all useful (local) storages in H3LP
 */
enum class Storages {
    USER_COOKIE, MEDICAL_INFO, SKILLS, EMERGENCIES_RECEIVED, FORUM_THEMES_NOTIFICATIONS, FORUM_CACHE,
    SIGN_IN, MSG_CACHE;

    private var ls: LocalStorage? = null
    private var isFresh = false

    companion object {

        const val SyncPref: String = "SyncPref"

        /**
         * Instantiates the storage of the corresponding type
         * If the storage has enabled online sync, it will fetch the data online at the first call
         * The storage is only pushed to the database after a push() call
         * @param choice The chosen database
         * @param context The context with which the storage is initialized
         * @return The instantiated storage of the required type
         */
        fun storageOf(choice: Storages, context: Context): LocalStorage {
            choice.ls = choice.ls ?: LocalStorage(choice.name, context)

            if (!choice.isFresh) {
                choice.ls?.pull(false)
                choice.isFresh = true
            }
            return choice.ls!!
        }

        /**
         * Reset local storage completely
         */
        fun resetStorage() {
            for (storage in values()) {
                storage.ls?.clearAll()
            }
        }

        /**
         * Disables online sync for all storages. Typically used for guests.
         * @param context The context to have access to the shared preferences
         */
        fun disableOnlineSync(context: Context) {
            for (s in values()) {
                s.setOnlineSync(false, context)
            }
        }
    }

    /**
     * Set the online synchronization for a given storage
     * @param isSyncEnabled If the synchronization must be enabled
     * @param context The context to have access to the shared preferences
     */
    fun setOnlineSync(isSyncEnabled: Boolean, context: Context) {
        context.getSharedPreferences("SyncPref", AppCompatActivity.MODE_PRIVATE)
            .edit().putString(name, isSyncEnabled.toString()).apply()
    }

    /**
     * Get the online synchronization for a given storage
     * @param context The context to have access to the shared preferences
     */
    fun getOnlineSync(context: Context): Boolean {
        return parseBoolean(
            context.getSharedPreferences("SyncPref", AppCompatActivity.MODE_PRIVATE)
                .getString(name, "true")
        )
    }

}