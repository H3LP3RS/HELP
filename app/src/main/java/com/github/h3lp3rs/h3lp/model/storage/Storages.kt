package com.github.h3lp3rs.h3lp.model.storage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.getGlobalCtx
import java.lang.Boolean.parseBoolean

/**
 * Enumeration of all useful (local) storages in H3LP
 */
@RequiresApi(Build.VERSION_CODES.S)
enum class Storages {
    USER_COOKIE, MEDICAL_INFO, SKILLS, EMERGENCIES_RECEIVED, FORUM_THEMES_NOTIFICATIONS, FORUM_CACHE,
    SIGN_IN, MSG_CACHE;

    private val ls = LocalStorage(name, getGlobalCtx())
    private var isFresh = false

    companion object {

        const val SyncPref: String = "SyncPref"

        /**
         * Instantiates the storage of the corresponding type
         * If the storage has enabled online sync, it will fetch the data online at the first call
         * The storage is only pushed to the database after a push() call
         * @param choice The chosen database
         * @return The instantiated storage of the required type
         */
        fun storageOf(choice: Storages): LocalStorage {
            if (!choice.isFresh) {
                choice.ls.pull(false)
                choice.isFresh = true
            }
            return choice.ls
        }

        /**
         * Reset local storage completely
         */
        fun resetStorage() {
            for (storage in values()) {
                storage.ls.clearAll()
            }
        }

        /**
         * Disables online sync for all storages. Typically used for guests.
         */
        fun disableOnlineSync() {
            for (s in values()) {
                s.setOnlineSync(false)
            }
        }
    }

    /**
     * Set the online synchronization for a given storage
     * @param isSyncEnabled if the synchronization must be enabled
     */
    fun setOnlineSync(isSyncEnabled: Boolean) {
        getGlobalCtx().getSharedPreferences("SyncPref", AppCompatActivity.MODE_PRIVATE)
            .edit().putString(name, isSyncEnabled.toString()).apply()
    }

    /**
     * Get the online synchronization for a given storage
     */
    fun getOnlineSync(): Boolean {
        return parseBoolean(
            getGlobalCtx().getSharedPreferences("SyncPref", AppCompatActivity.MODE_PRIVATE)
                .getString(name, "true")
        )
    }

}