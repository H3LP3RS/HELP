package com.github.h3lp3rs.h3lp.model.database

import android.content.Context

/**
 * Enumeration of all useful databases in H3LP
 */
enum class Databases {
    PREFERENCES, EMERGENCIES, NEW_EMERGENCIES, MESSAGES, CONVERSATION_IDS, PRO_USERS, RATINGS, REPORTS;

    private var db: Database? = null // Var to enable test-time mocking

    companion object {
        /**
         * Instantiates the database of the corresponding type (the default database is with
         * Firebase, unless set otherwise)
         * @param choice The chosen database
         * @param context The context of the calling activity as required by FireDatabase
         * @return The actual database, as set in setDatabase, or a FireDatabase if using the default
         * database
         */
        fun databaseOf(choice: Databases, context: Context): Database {
            choice.db = choice.db ?: FireDatabase(choice.name, context)
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