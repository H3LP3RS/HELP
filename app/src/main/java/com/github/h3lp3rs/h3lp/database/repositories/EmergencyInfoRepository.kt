package com.github.h3lp3rs.h3lp.database.repositories

import com.github.h3lp3rs.h3lp.database.models.EmergencyInformation
import com.github.h3lp3rs.h3lp.database.Database
import java.util.concurrent.CompletableFuture

/**
 * Repository for emergency information communication with the database
 */
class EmergencyInfoRepository(private val database: Database) : Repository<EmergencyInformation>{

    override fun get(id: Int): CompletableFuture<EmergencyInformation> {
        return database.getObject(id.toString(), EmergencyInformation::class.java)
    }

    override fun insert(value: EmergencyInformation) {
        database.setObject(value.id.toString(), EmergencyInformation::class.java, value)
    }

    override fun delete(id: Int){
        database.delete(id.toString())
    }
}