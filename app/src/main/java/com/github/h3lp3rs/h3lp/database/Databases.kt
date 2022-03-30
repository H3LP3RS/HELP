package com.github.h3lp3rs.h3lp.database

/**
 * Enumeration of all useful databases in H3LP
 */
enum class Databases {
    PREFERENCES, EMERGENCIES, NEW_EMERGENCIES;
    var db: Database = FireDatabase(name) // Var to enable test-time mocking
    companion object {
        /**
         * Instantiates the database of the corresponding type
         * @param choice The chosen database
         */
        fun databaseOf(choice: Databases): Database {
            return choice.db
        }
    }
}