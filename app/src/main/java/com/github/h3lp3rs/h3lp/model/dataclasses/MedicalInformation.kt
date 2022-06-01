package com.github.h3lp3rs.h3lp.model.dataclasses

import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.time.Year

/**
 * Class representing some medical information about the user, this information can then be used by
 * the helper or emergency services when they come to help, for example to not give the person in
 * need of help some medication they're allergic to
 *
 * @param size The user's size in cm
 * @param weight The user's weight in kg
 * @param gender The user's gender
 * @param yearOfBirth The user's year of birth
 * @param conditions Specific conditions the user suffers from
 * @param allergy Allergies the user has (to avoid giving them medication containing those allergens)
 * @param bloodType The user's blood type to be able to give them the correct blood type
 * @param emergencyContactPrimaryName The name of the user's emergency contact
 * @param emergencyContactNumber The phone number of the user's emergency contact
 */
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
) {
    init {
        require(size in MIN_HEIGHT..MAX_HEIGHT)
        require(weight in MIN_WEIGHT..MAX_WEIGHT)
        require(yearOfBirth in MIN_YEAR..Year.now().value - ADULT_AGE)
        if (emergencyContactNumber != EMPTY_NB) {
            try {
                val number =
                    PhoneNumberUtil.getInstance().parse(emergencyContactNumber, DEFAULT_COUNTRY)
                require(PhoneNumberUtil.getInstance().isPossibleNumber(number))
            } catch (e: Exception) {
                throw IllegalArgumentException(e.message)
            }
        }
    }

    companion object {
        const val MAX_WEIGHT = 500
        const val MIN_WEIGHT = 20
        const val MAX_HEIGHT = 240
        const val MIN_HEIGHT = 40
        const val MIN_YEAR = 1900

        // To only allow adult users (for legal purposes, we cannot allow non-adults to help / be
        // helped since they are still under the protection of their guardian)
        const val ADULT_AGE = 18

        // This constant is used to determine the default country specifier
        // when the emergency contact is entered. This could be made location
        // dependant in the future.
        const val DEFAULT_COUNTRY = "CH"
        const val EMPTY_NB = ""
    }
}

/**
 * Enum representing gender options (or NoChoice if the user doesn't want to give that information)
 * @param genderText A textual representation of the corresponding gender
 */
enum class Gender(val genderText: String) {
    Female("Female"), Male("Male"), Other("Other"), NoChoice("Prefer not to say");
}

enum class BloodType(val type: String) {
    Op("O+"), On("O-"), Ap("A+"), An("A-"), ABp("AB+"), ABn("AB-"), Bp("B+"), Bn("B-"), Unknown("Unknown")
}