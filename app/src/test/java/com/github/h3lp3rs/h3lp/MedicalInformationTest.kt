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

    @Test
    fun validMedicalInfoWork(){
        val medicalInformation = MedicalInformation(150,70,Gender.Man,2000,"condition","treatment","allergy",BloodType.ABn)
        assertEquals(medicalInformation.size,150)
        assertEquals(medicalInformation.weight,70)
        assertEquals(medicalInformation.gender,Gender.Man)
        assertEquals(medicalInformation.yearOfBirth,2000)
        assertEquals(medicalInformation.allergy,"allergy")
        assertEquals(medicalInformation.conditions,"condition")
        assertEquals(medicalInformation.actualTreatment,"treatment")
        assertEquals(medicalInformation.bloodType,BloodType.ABn)
    }
    @Test
    fun bloodTypeStringWork(){
        assertEquals(BloodType.ABn.type,"AB-")
        assertEquals(BloodType.Op.type,"O+")
        assertEquals(BloodType.An.type,"A-")
    }

    @Test
    fun genderTypeStringWork(){
        assertEquals(Gender.Man.sex,"Male")
        assertEquals(Gender.Woman.sex,"Female")
    }





}