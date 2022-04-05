package com.github.h3lp3rs.h3lp.database.repositories

import com.github.h3lp3rs.h3lp.database.Databases

val emergencyInfoRepository = EmergencyInfoRepository(Databases.databaseOf(Databases.EMERGENCIES))