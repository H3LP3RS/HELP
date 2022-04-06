package com.github.h3lp3rs.h3lp.database.repositories

import com.github.h3lp3rs.h3lp.database.Databases

/**
 * File with all the repositories
 */
val emergencyInfoRepository = EmergencyInfoRepository(Databases.databaseOf(Databases.EMERGENCIES))