package com.github.h3lp3rs.h3lp.database.repositories

import com.github.h3lp3rs.h3lp.database.models.EmergencyInformation
import com.github.h3lp3rs.h3lp.database.Database
import java.util.concurrent.CompletableFuture

class EmergencyInfoRepository(private val database: Database) {

    fun get(id: String): CompletableFuture<EmergencyInformation> {
        return database.getObject(id, EmergencyInformation::class.java)
    }

    fun create(info: EmergencyInformation) {
        return database.setObject(info.id, EmergencyInformation::class.java, info)
    }
}