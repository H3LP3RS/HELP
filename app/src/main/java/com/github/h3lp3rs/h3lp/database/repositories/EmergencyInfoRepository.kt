package com.github.h3lp3rs.h3lp.database.repositories

import com.github.h3lp3rs.h3lp.database.models.EmergencyInformation
import com.github.h3lp3rs.h3lp.database.Database
import java.util.concurrent.CompletableFuture

/**
 * Repository for emergency information communication with the database
 */
class EmergencyInfoRepository(private val database: Database) : Repository<EmergencyInformation>{

    override fun get(id: String): CompletableFuture<EmergencyInformation> {
        return database.getObject(id, EmergencyInformation::class.java)
    }

    override fun create(value: EmergencyInformation) {
        database.setObject(value.id, EmergencyInformation::class.java, value)
    }

    override fun delete(id: String){
        database.delete(id)
    }
}