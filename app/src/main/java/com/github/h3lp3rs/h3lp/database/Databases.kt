package com.github.h3lp3rs.h3lp.database

import android.content.Intent
import android.os.Bundle
import com.github.h3lp3rs.h3lp.*
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.createNotificationChannel
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.sendIntentNotification
import com.github.h3lp3rs.h3lp.notification.NotificationService.Companion.sendOpenActivityNotification
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.storage.Storages

/**
 * Enumeration of all useful databases in H3LP
 */
enum class Databases {
    PREFERENCES, EMERGENCIES, NEW_EMERGENCIES;
    var db: Database = FireDatabase(name) // Var to enable test-time mocking
    companion object {
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
            val storage = Storages.storageOf(Storages.SKILLS)
            val skills = storage.getObjectOrDefault(globalContext.getString(R.string.my_skills_key), HelperSkills::class.java, null)
            if(skills == null) return
            else {
                val db = databaseOf(NEW_EMERGENCIES)
                if(skills.hasVentolin) {
                    db.addListener(globalContext.getString(R.string.asthma_med), Int::class.java) { id ->
                        val emergency = databaseOf(EMERGENCIES).getObject(id.toString(), EmergencyInformation::class.java)
                        emergency.thenAccept {
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
    }
}