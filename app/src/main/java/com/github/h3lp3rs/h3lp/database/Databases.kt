package com.github.h3lp3rs.h3lp.database

/**
 * Enumeration of all useful databases in H3LP
 */
enum class Databases {
    PREFERENCES, EMERGENCIES, NEW_EMERGENCIES, MESSAGES, CONVERSATION_IDS;
    private var db: Database? = null // Var to enable test-time mocking
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