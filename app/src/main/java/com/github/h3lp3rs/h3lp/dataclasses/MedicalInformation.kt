package com.github.h3lp3rs.h3lp

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.lang.IllegalArgumentException
import java.time.Year

@RequiresApi(Build.VERSION_CODES.O)
class MedicalInformation(
    val size: Int,
    val weight: Int,
    val gender: Gender,
    val yearOfBirth: Int,
    val conditions: String,
    val actualTreatment: String,
    val allergy: String,
    val bloodType: BloodType,
    val emergencyContactPrimaryName: String,
    val emergencyContactNumber: String,
){
    init {
        require(size in MIN_HEIGHT..MAX_HEIGHT)
        require(weight in MIN_WEIGHT..MAX_WEIGHT)
        require(yearOfBirth in MIN_YEAR..Year.now().value)
        try {
            val number = PhoneNumberUtil.getInstance().parse(emergencyContactNumber, "CH")
            require( PhoneNumberUtil.getInstance().isPossibleNumber(number))
        } catch(e: Exception){
            throw IllegalArgumentException(e.message)
        }
    }
    companion object{
        const val MAX_WEIGHT = 500
        const val MIN_WEIGHT= 20
        const val MAX_HEIGHT=240
        const val MIN_HEIGHT=40
        const val MIN_YEAR=1900
    }
}

enum class Gender() {
    Male,Female;
}
enum class BloodType(val type : String){
    Op("O+"),On("O-"), Ap("A+"),An("A-"), ABp("AB+"),ABn("AB-"), Bp("B+"),Bn("B-"),Unkown("Unknown")
}