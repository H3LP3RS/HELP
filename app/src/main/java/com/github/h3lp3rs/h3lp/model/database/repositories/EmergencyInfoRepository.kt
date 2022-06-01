package com.github.h3lp3rs.h3lp.model.database.repositories

import com.github.h3lp3rs.h3lp.model.database.Database
import com.github.h3lp3rs.h3lp.model.dataclasses.EmergencyInformation
import java.util.concurrent.CompletableFuture

/**
 * Repository for emergency information communication with the database
 * @param database The database from which to create the repository
 */
class EmergencyInfoRepository(private val database: Database) : Repository<EmergencyInformation> {

    override fun get(id: String): CompletableFuture<EmergencyInformation> {
        return database.getObject(id, EmergencyInformation::class.java)
    }

    override fun insert(value: EmergencyInformation) {
        database.setObject(value.id, EmergencyInformation::class.java, value)
    }

    override fun delete(id: String) {
        database.delete(id)
    }
}