package com.github.h3lp3rs.h3lp.storage

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getGlobalCtx

/**
 * Enumeration of all useful (local) storages in H3LP
 */
enum class Storages(enableOnlineSync: Boolean) {
    USER_COOKIE(true), MEDICAL_INFO(false);

    private val ls = LocalStorage(name, getGlobalCtx(), enableOnlineSync)
    private var isFresh = false

    companion object{
        /**
         * Instantiates the storage of the corresponding type
         * @param choice The chosen database
         */
        fun storageOf(choice: Storages): LocalStorage {
            val db = Databases.databaseOf(Databases.PREFERENCES)
            db.setString("coucou", "uid2")
            if (!choice.isFresh){
                choice.ls.pull()
                choice.isFresh = true
            }
            return choice.ls
        }
    }
}