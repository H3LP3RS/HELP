package com.github.h3lp3rs.h3lp.database

import android.content.Intent
import android.os.Bundle
import com.github.h3lp3rs.h3lp.*
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.createNotificationChannel
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.sendIntentNotification
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf

/**
 * Enumeration of all useful databases in H3LP
 */
enum class Databases {
    PREFERENCES, EMERGENCIES, NEW_EMERGENCIES;
    var db: Database = FireDatabase(name) // Var to enable test-time mocking
    companion object {
        private val idRegistered = HashSet<String>()
        /**
         * Instantiates the database of the corresponding type
         * @param choice The chosen database
         */
        fun databaseOf(choice: Databases): Database {
            return choice.db
        }

        /**
         * Activates all listeners for helpees the helper may help
         */
        fun activateHelpListeners() {
            val storage = storageOf(Storages.SKILLS)
            val skills = storage.getObjectOrDefault(globalContext.getString(R.string.my_skills_key), HelperSkills::class.java, null)
            if(skills == null) return
            else {
                val db = databaseOf(NEW_EMERGENCIES)
                // Utility function to add a specific listener to a specific key if present
                fun activateListener(isPresent: Boolean, key: String) {
                    if(isPresent) {
                        db.addListenerIfNotPresent(key, Int::class.java) { id ->
                            val emergency = databaseOf(EMERGENCIES).getObject(id.toString(), EmergencyInformation::class.java)
                            emergency.thenAccept {
                                if(!idRegistered.contains(it.id)) {
                                    idRegistered.add(it.id)
                                    createNotificationChannel(globalContext)
                                    val intent = Intent(globalContext, HelpPageActivity::class.java)
                                    val b = Bundle()
                                    b.putString(EXTRA_EMERGENCY_KEY, it.id)
                                    b.putStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS, it.meds)
                                    b.putDouble(EXTRA_DESTINATION_LAT, it.latitude)
                                    b.putDouble(EXTRA_DESTINATION_LONG, it.longitude)
                                    intent.putExtras(b)
                                    sendIntentNotification(globalContext, globalContext.getString(R.string.emergency),
                                        globalContext.getString(R.string.need_help), intent)
                                }
                            }
                        }
                    }
                }
                // Chain of listener instantiations
                activateListener(skills.hasVentolin, globalContext.getString(R.string.asthma_med))
                activateListener(skills.isMedicalPro, globalContext.getString(R.string.med_pro))
                activateListener(skills.hasEpipen, globalContext.getString(R.string.epipen))
                activateListener(skills.hasInsulin, globalContext.getString(R.string.Insulin))
                activateListener(skills.hasFirstAidKit, globalContext.getString(R.string.first_aid_kit))
                activateListener(skills.knowsCPR, globalContext.getString(R.string.cpr))
            }
        }
    }
}