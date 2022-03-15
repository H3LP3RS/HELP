package com.github.h3lp3rs.h3lp

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class MedicalInformationTest {

    @Test
    fun badSizeThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(MedicalInformation.MIN_HEIGHT-1,MedicalInformation.MAX_WEIGHT-1,Gender.Male,MedicalInformation.MIN_YEAR+1,"","","",BloodType.ABn)
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(MedicalInformation.MAX_HEIGHT+1,MedicalInformation.MAX_WEIGHT-1,Gender.Male,MedicalInformation.MIN_YEAR+1,"","","",BloodType.ABn)
        }
    }
    @Test
    fun badWeightThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(MedicalInformation.MAX_HEIGHT-1,MedicalInformation.MIN_WEIGHT-1,Gender.Male,MedicalInformation.MIN_YEAR+1,"","","",BloodType.ABn)
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(MedicalInformation.MAX_HEIGHT-1,MedicalInformation.MAX_WEIGHT+1,Gender.Male,MedicalInformation.MIN_YEAR+1,"","","",BloodType.ABn)
        }
    }
    @Test
    fun badYearThrowIAE() {
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(MedicalInformation.MAX_HEIGHT-1,MedicalInformation.MAX_WEIGHT-1,Gender.Male,MedicalInformation.MIN_YEAR-1,"","","",BloodType.ABn)
        }
        assertThrows(IllegalArgumentException::class.java) {
            MedicalInformation(MedicalInformation.MAX_HEIGHT-1,MedicalInformation.MAX_WEIGHT-1,Gender.Male,Calendar.getInstance().get(Calendar.YEAR)+1,"","","",BloodType.ABn)
        }
    }

    @Test
    fun validMedicalInfoWork(){
        val medicalInformation = MedicalInformation(MedicalInformation.MAX_HEIGHT-1,MedicalInformation.MAX_WEIGHT-1,Gender.Male,
            Calendar.getInstance().get(Calendar.YEAR)-1,"condition","treatment","allergy",BloodType.ABn)
        assertEquals(medicalInformation.weight,MedicalInformation.MAX_WEIGHT-1)
        assertEquals(medicalInformation.gender,Gender.Male)
        assertEquals(medicalInformation.yearOfBirth,Calendar.getInstance().get(Calendar.YEAR)-1)
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
    fun testing(){

        assertEquals(BloodType.ABn,BloodType.valueOf("ABn"))
    }





}