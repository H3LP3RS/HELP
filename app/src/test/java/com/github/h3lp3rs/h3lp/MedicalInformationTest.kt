package com.github.h3lp3rs.h3lp

import org.junit.Test

import org.junit.Assert.*

class MedicalInformationTest {
    @Test
    fun badSizeThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(10,70,Gender.Man,2000,"","","",BloodType.ABn)
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(300,70,Gender.Man,2000,"","","",BloodType.ABn)
        }
    }
    @Test
    fun badWeightThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(150,10,Gender.Man,2000,"","","",BloodType.ABn)
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(150,505,Gender.Man,2000,"","","",BloodType.ABn)
        }
    }
    @Test
    fun badYearThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(150,70,Gender.Man,1890,"","","",BloodType.ABn)
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(150,70,Gender.Man,2032,"","","",BloodType.ABn)
        }
    }

}