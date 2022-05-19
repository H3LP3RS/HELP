package com.github.h3lp3rs.h3lp.database

import android.content.Intent
import android.os.Bundle
import com.github.h3lp3rs.h3lp.*
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.createNotificationChannel
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.sendIntentNotification
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf

/**
 * Enumeration of all useful databases in H3LP
 */
enum class Databases {
    PREFERENCES, EMERGENCIES, NEW_EMERGENCIES, MESSAGES, CONVERSATION_IDS, PRO_USERS, FORUM, RATINGS;

    var db: Database? = null // Var to enable test-time mocking
    companion object {
        /**
         * Instantiates the database of the corresponding type (the default database is with
         * Firebase, unless set otherwise)
         *
         * @param choice The chosen database
         */
        fun databaseOf(choice: Databases): Database {
            choice.db = choice.db ?: FireDatabase(choice.name)
            return choice.db!!
        }

        /**
         * Used for testing purposes to give database instances
         * @param newDatabase The database to use
         */
        fun setDatabase(choice: Databases, newDatabase: Database) {
            choice.db = newDatabase
        }
    }
}