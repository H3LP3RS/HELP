package com.github.h3lp3rs.h3lp.profile

import com.github.h3lp3rs.h3lp.model.dataclasses.BloodType
import com.github.h3lp3rs.h3lp.model.dataclasses.Gender
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.ADULT_AGE
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.MAX_HEIGHT
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.MAX_WEIGHT
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.MIN_HEIGHT
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.MIN_WEIGHT
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.MIN_YEAR
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.*

class MedicalInformationTest {
    private val validNumber = "+41216933000"

    @Test
    fun badSizeThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(
                MIN_HEIGHT - 1, MAX_WEIGHT - 1, Gender.Male,
                MIN_YEAR + 1, "", "", "", BloodType.ABn, "", validNumber
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(
                MAX_HEIGHT + 1, MAX_WEIGHT - 1, Gender.Male,
                MIN_YEAR + 1, "", "", "", BloodType.ABn, "", validNumber
            )
        }
    }

    @Test
    fun badWeightThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(
                MAX_HEIGHT - 1,
                MIN_WEIGHT - 1,
                Gender.Male,
                MIN_YEAR + 1,
                "",
                "",
                "",
                BloodType.ABn,
                "",
                validNumber
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(
                MAX_HEIGHT - 1, MAX_WEIGHT + 1, Gender.Male,
                MIN_YEAR + 1, "", "", "", BloodType.ABn, "", validNumber
            )
        }
    }

    @Test
    fun badYearThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(
                MAX_HEIGHT - 1, MAX_WEIGHT - 1, Gender.Male,
                MIN_YEAR - 1, "", "", "", BloodType.ABn, "", validNumber
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(
                MAX_HEIGHT - 1,
                MAX_WEIGHT - 1,
                Gender.Male,
                Calendar.getInstance().get(Calendar.YEAR) + 1,
                "",
                "",
                "",
                BloodType.ABn,
                "",
                validNumber
            )
        }
    }

    @Test
    fun badNumberThrowsIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(
                MAX_HEIGHT - 1, MAX_WEIGHT - 1, Gender.Male,
                MIN_YEAR + 1, "", "", "", BloodType.ABn, "", "wrong number"
            )
        }
    }

    @Test
    fun validMedicalInfoWork() {
        val medicalInformation = MedicalInformation(
            MAX_HEIGHT - 1,
            MAX_WEIGHT - 1,
            Gender.Male,
            Calendar.getInstance().get(Calendar.YEAR) - ADULT_AGE,
            "condition",
            "treatment",
            "allergy",
            BloodType.ABn,
            "",
            validNumber
        )
        assertEquals(medicalInformation.weight, MAX_WEIGHT - 1)
        assertEquals(medicalInformation.gender, Gender.Male)
        assertEquals(
            medicalInformation.yearOfBirth,
            Calendar.getInstance().get(Calendar.YEAR) - ADULT_AGE
        )
        assertEquals(medicalInformation.allergy, "allergy")
        assertEquals(medicalInformation.conditions, "condition")
        assertEquals(medicalInformation.actualTreatment, "treatment")
        assertEquals(medicalInformation.bloodType, BloodType.ABn)
    }

    @Test
    fun bloodTypeStringWork() {
        assertEquals(BloodType.ABn.type, "AB-")
        assertEquals(BloodType.Op.type, "O+")
        assertEquals(BloodType.An.type, "A-")
    }

}