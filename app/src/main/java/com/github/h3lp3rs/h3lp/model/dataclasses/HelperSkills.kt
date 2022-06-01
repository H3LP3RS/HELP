package com.github.h3lp3rs.h3lp.model.dataclasses

/**
 * Data class representing the information on a specific helper's skills and the medication they
 * usually carry around. This is to be able to see for which kind of emergencies they can help and
 * for which they don't have the necessary skills / medication available
 *
 * @param hasEpipen True if the user usually has an epipen with them, false otherwise
 * @param hasVentolin True if the user usually has a ventolin with them, false otherwise
 * @param hasInsulin True if the user usually has insulin with them, false otherwise
 * @param knowsCPR True if the user knows how to perform CPR, false otherwise
 * @param hasFirstAidKit True if the user usually has a first aid kit with them, false otherwise
 * @param isMedicalPro True if the user is a medical professional, false otherwise
 */
data class HelperSkills(
    val hasEpipen: Boolean, val hasVentolin: Boolean, val hasInsulin: Boolean,
    val knowsCPR: Boolean, val hasFirstAidKit: Boolean, val isMedicalPro: Boolean
)