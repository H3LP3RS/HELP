package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.database.repositories.EmergencyInfoRepository
import com.github.h3lp3rs.h3lp.database.repositories.Repository
import com.github.h3lp3rs.h3lp.dataclasses.*
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation.Companion.ADULT_AGE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class EmergencyInfoRepositoryTest {
    private val validNumber = "0216933000"
    private val repository: Repository<EmergencyInformation> =
        EmergencyInfoRepository(MockDatabase())
    private val testKey = "KEY"
    private val testDoubleValue = 0.0
    private val doubleDelta = 1e-15
    private val skills = HelperSkills(false, false, false, false, false, true)
    private val testObject =
        EmergencyInformation(testKey, testDoubleValue, testDoubleValue, skills, ArrayList(), Date(), MedicalInformation(
            MedicalInformation.MAX_HEIGHT-1,
            MedicalInformation.MAX_WEIGHT-1,
            Gender.Male,
            Calendar.getInstance().get(Calendar.YEAR)-ADULT_AGE,"condition","treatment","allergy",
            BloodType.ABn, "", validNumber), ArrayList())

    @Test
    fun createdObjectIsCorrect() {
        repository.insert(testObject)
        val res = repository.get(testKey).get()
        assertEquals(res.latitude, testDoubleValue, doubleDelta)
        assertEquals(res.longitude, testDoubleValue, doubleDelta)
        assertEquals(res.skills, skills)
    }


    @Test
    fun deleteObjectWorks() {
        repository.insert(testObject)
        repository.delete(testKey)
        val res = repository.get(testKey)
        assertTrue(res.isCompletedExceptionally)
    }
}