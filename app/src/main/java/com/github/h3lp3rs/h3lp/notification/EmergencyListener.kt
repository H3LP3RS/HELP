package com.github.h3lp3rs.h3lp.notification

import android.content.Intent
import android.os.Bundle
import com.github.h3lp3rs.h3lp.*
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.storage.Storages

object EmergencyListener {

    /**
     * Activates all listeners for helpees the helper may help
     */
    fun activateListeners() {
        val skillStorage = Storages.storageOf(Storages.SKILLS)
        val skills = skillStorage.getObjectOrDefault(SignInActivity.globalContext.getString(R.string.my_skills_key), HelperSkills::class.java, null)
        if(skills == null) return
        else {
            val db = Databases.databaseOf(Databases.NEW_EMERGENCIES)
            // Utility function to add a specific listener to a specific key if present
            fun activateListener(isPresent: Boolean, key: String) {
                if(isPresent) {
                    db.addListenerIfNotPresent(key, Int::class.java) { id ->
                        // Look if we already encountered this id
                        val emergencyStorage = Storages.storageOf(Storages.EMERGENCIES_RECEIVED)
                        if(emergencyStorage.getBoolOrDefault(id.toString(), false)) {
                            return@addListenerIfNotPresent
                        }
                        // Never see this emergency again later
                        emergencyStorage.setBoolean(id.toString(), true)
                        // Send notification only if the associated object still exists
                        databaseOf(Databases.EMERGENCIES)
                            .getObject(id.toString(), EmergencyInformation::class.java)
                            .thenAccept {
                                NotificationService.createNotificationChannel(SignInActivity.globalContext)
                                val intent = Intent(SignInActivity.globalContext, HelperPageActivity::class.java)
                                // Data to transfer to the help page activity
                                val bundle = Bundle()
                                bundle.putString(EXTRA_EMERGENCY_KEY, it.id)
                                bundle.putStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS, it.meds)
                                bundle.putDouble(EXTRA_DESTINATION_LAT, it.latitude)
                                bundle.putDouble(EXTRA_DESTINATION_LONG, it.longitude)
                                intent.putExtras(bundle)
                                NotificationService.sendIntentNotification(
                                    SignInActivity.globalContext,
                                    SignInActivity.globalContext.getString(R.string.emergency),
                                    SignInActivity.globalContext.getString(R.string.need_help),
                                    intent
                                )
                            }
                    }
                }
            }
            // Chain of listener instantiations
            activateListener(skills.hasVentolin, SignInActivity.globalContext.getString(R.string.asthma_med))
            activateListener(skills.isMedicalPro, SignInActivity.globalContext.getString(R.string.med_pro))
            activateListener(skills.hasEpipen, SignInActivity.globalContext.getString(R.string.epipen))
            activateListener(skills.hasInsulin, SignInActivity.globalContext.getString(R.string.Insulin))
            activateListener(skills.hasFirstAidKit, SignInActivity.globalContext.getString(R.string.first_aid_kit))
            activateListener(skills.knowsCPR, SignInActivity.globalContext.getString(R.string.cpr))
        }
    }
}