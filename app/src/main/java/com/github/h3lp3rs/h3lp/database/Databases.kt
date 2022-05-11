package com.github.h3lp3rs.h3lp.database

import android.content.Intent
import android.os.Bundle
import com.github.h3lp3rs.h3lp.*
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.createNotificationChannel
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.sendIntentNotification
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf

/**
 * Enumeration of all useful databases in H3LP
 */
enum class Databases {
    PREFERENCES, EMERGENCIES, NEW_EMERGENCIES, MESSAGES, CONVERSATION_IDS, PRO_USERS, FORUM;
    var db: Database? = null // Var to enable test-time mocking
    companion object {
        /**
         * Instantiates the database of the corresponding type (the default database is with
         * Firebase, unless set otherwise)
         *
         * @param choice The chosen database
         */
        fun databaseOf(choice: Databases): Database {
            choice.db = choice.db ?: FireDatabase(choice.name)
            return choice.db!!
        }

        /**
         * Used for testing purposes to give database instances
         * @param newDatabase The database to use
         */
        fun setDatabase(choice: Databases, newDatabase: Database) {
            choice.db = newDatabase
        }

        /**
         * Activates all listeners for helpees the helper may help
         */
        fun activateHelpListeners() {
            val skillStorage = storageOf(SKILLS)
            val skills = skillStorage.getObjectOrDefault(globalContext.getString(R.string.my_skills_key), HelperSkills::class.java, null)
            if(skills == null) return
            else {
                val db = databaseOf(NEW_EMERGENCIES)
                // Utility function to add a specific listener to a specific key if present
                fun activateListener(isPresent: Boolean, key: String) {
                    if(isPresent) {
                        db.addListenerIfNotPresent(key, Int::class.java) { id ->
                            // Look if we already encountered this id
                            val emergencyStorage = storageOf(EMERGENCIES_RECEIVED)
                            if(emergencyStorage.getBoolOrDefault(id.toString(), false)) {
                                return@addListenerIfNotPresent
                            }
                            // Never see this emergency again later
                            emergencyStorage.setBoolean(id.toString(), true)
                            // Send notification only if the associated object still exists
                            databaseOf(EMERGENCIES).getObject(id.toString(), EmergencyInformation::class.java)
                                .thenAccept {
                                    createNotificationChannel(globalContext)
                                    val intent = Intent(globalContext, HelpPageActivity::class.java)
                                    // Data to transfer to the help page activity
                                    val bundle = Bundle()
                                    bundle.putString(EXTRA_EMERGENCY_KEY, it.id)
                                    bundle.putStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS, it.meds)
                                    bundle.putDouble(EXTRA_DESTINATION_LAT, it.latitude)
                                    bundle.putDouble(EXTRA_DESTINATION_LONG, it.longitude)
                                    bundle.putString(EXTRA_HELPEE_ID, "test_end_to_end") //TODO adapt this later
                                    intent.putExtras(bundle)
                                    sendIntentNotification(globalContext, globalContext.getString(R.string.emergency),
                                        globalContext.getString(R.string.need_help), intent)
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