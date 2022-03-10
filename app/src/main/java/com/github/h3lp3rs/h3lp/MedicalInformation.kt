package com.github.h3lp3rs.h3lp

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Year

@RequiresApi(Build.VERSION_CODES.O)
class MedicalInformation(
    val size: Int,val weight: Int,val gender: Gender, val yearOfBirth: Int, val conditions: String, val actualTreatment: String, val allergy: String, val bloodType: BloodType,
){
    init {
        require(size in R.integer.minHeight..R.integer.maxHeight)
        require(weight in R.integer.minWeight..R.integer.maxWeight)
        require(yearOfBirth in R.integer.minYear..Year.now().value)
    }
}

enum class Gender(val sex : String) {
    Man("Male"),Woman("Female")
}

enum class BloodType(val type : String){
    Op("O+"),On("O-"), Ap("A+"),An("A-"), ABp("AB+"),ABn("AB-"), Bp("B+"),Bn("B-"),Unkown("Unknown")
}