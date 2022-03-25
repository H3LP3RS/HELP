package com.github.h3lp3rs.h3lp.database

/**
 * Enumeration of all useful databases in H3LP
 */
enum class Databases {
    PREFERENCES, EMERGENCIES, NEW_EMERGENCIES;
    val db: Database = FireDatabase(name)
    companion object {
        fun databaseOf(choice: Databases): Database {
            return choice.db
        }
    }
}