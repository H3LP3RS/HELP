package com.github.h3lp3rs.h3lp.storage

import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getGlobalCtx
import java.lang.Boolean.parseBoolean

/**
 * Enumeration of all useful (local) storages in H3LP
 */
enum class Storages() {
    USER_COOKIE(), MEDICAL_INFO(), SKILLS(),EMERGENCIES_RECEIVED();

    private val ls = LocalStorage(name, getGlobalCtx())
    private var isFresh = false

    companion object {

        const val SyncPref: String = "SyncPref"

        /**
         * Instantiates the storage of the corresponding type
         * If the storage has enabled online sync, it will fetch the data online at the first call
         * The following storages have the online sync enabled:
         * - USER_COOKIE
         * The storage is only pushed to the database after a push() call
         * @param choice The chosen database
         */
        fun storageOf(choice: Storages): LocalStorage {
            if (!choice.isFresh) {
                choice.ls.pull()
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
    }

    /**
     * set the Online synchronization for a given storage
     * @param isSyncEnable if the synchronization must be enabled
     */
    fun setOnlineSync(isSyncEnable: Boolean) {
        getGlobalCtx().getSharedPreferences("SyncPref", AppCompatActivity.MODE_PRIVATE)
            .edit().putString(name, isSyncEnable.toString()).apply()
    }

    /**
     * get the Online synchronization for a given storage
     */
    fun getOnlineSync(): Boolean {
        return parseBoolean(
            getGlobalCtx().getSharedPreferences("SyncPref", AppCompatActivity.MODE_PRIVATE)
                .getString(name, "true")
        )
    }

}