package com.github.h3lp3rs.h3lp.storage

import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getGlobalCtx

/**
 * Enumeration of all useful (local) storages in H3LP
 */
enum class Storages(enableOnlineSync: Boolean) {
    USER_COOKIE(true), MEDICAL_INFO(false), SKILLS(true),
    EMERGENCIES_RECEIVED(false);

    private val ls = LocalStorage(name, getGlobalCtx(), enableOnlineSync)
    private var isFresh = false

    companion object{
        /**
         * Instantiates the storage of the corresponding type
         * If the storage has enabled online sync, it will fetch the data online at the first call
         * The following storages have the online sync enabled:
         * - USER_COOKIE
         * The storage is only pushed to the database after a push() call
         * @param choice The chosen database
         */
        fun storageOf(choice: Storages): LocalStorage {
            if (!choice.isFresh){
                choice.ls.pull()
                choice.isFresh = true
            }
            return choice.ls
        }

        /**
         * Reset local storage completely
         */
        fun resetStorage() {
            for(storage in values()) {
                storage.ls.clearAll()
            }
        }
    }
}