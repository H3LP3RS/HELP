package com.github.h3lp3rs.h3lp.model.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.h3lp3rs.h3lp.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.EMERGENCIES
import com.github.h3lp3rs.h3lp.model.database.Databases.NEW_EMERGENCIES
import com.github.h3lp3rs.h3lp.model.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.model.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.model.notifications.NotificationService.Companion.createNotificationChannel
import com.github.h3lp3rs.h3lp.model.notifications.NotificationService.Companion.sendIntentNotification
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.model.storage.Storages.EMERGENCIES_RECEIVED
import com.github.h3lp3rs.h3lp.model.storage.Storages.SKILLS
import com.github.h3lp3rs.h3lp.view.helprequest.helpee.EXTRA_EMERGENCY_KEY
import com.github.h3lp3rs.h3lp.view.helprequest.helper.EXTRA_DESTINATION_LAT
import com.github.h3lp3rs.h3lp.view.helprequest.helper.EXTRA_DESTINATION_LONG
import com.github.h3lp3rs.h3lp.view.helprequest.helper.EXTRA_HELP_REQUIRED_PARAMETERS
import com.github.h3lp3rs.h3lp.view.helprequest.helper.HelperPageActivity

object EmergencyListener {

    // Maximum helpee-helper distance in meters
    private const val MAX_DISTANCE = 5000

    /**
     * Activates all listeners for helpees the helper may help
     * @param context The context of the activity which activated the listeners (to instantiate
     * the skills and emergencies local storages)
     */
    fun activateListeners(context: Context) {
        val skillStorage = storageOf(SKILLS, context)
        val skills = skillStorage.getObjectOrDefault(
            context.getString(R.string.my_skills_key),
            HelperSkills::class.java,
            null
        )
        if (skills == null) return
        else {
            val db = databaseOf(NEW_EMERGENCIES, context)

            // Utility function to add a specific listener to a specific key if present
            fun activateListener(isPresent: Boolean, key: String) {
                if (isPresent) {
                    db.addListenerIfNotPresent(key, Int::class.java) { id ->
                        // Look if we already encountered this id
                        val emergencyStorage = storageOf(EMERGENCIES_RECEIVED, context)
                        if (emergencyStorage.getBoolOrDefault(id.toString(), false)) {
                            return@addListenerIfNotPresent
                        }
                        // Never see this emergency again later
                        emergencyStorage.setBoolean(id.toString(), true)

                        // Send notification only if the associated object still exists
                        databaseOf(EMERGENCIES, context)
                            .getObject(id.toString(), EmergencyInformation::class.java)
                            .thenAccept {
                                createNotificationChannel(context)
                                val intent = Intent(
                                    context,
                                    HelperPageActivity::class.java
                                )

                                // Data to transfer to the help page activity
                                val bundle = Bundle()
                                bundle.putString(EXTRA_EMERGENCY_KEY, it.id)
                                bundle.putStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS, it.meds)
                                bundle.putDouble(EXTRA_DESTINATION_LAT, it.latitude)
                                bundle.putDouble(EXTRA_DESTINATION_LONG, it.longitude)
                                intent.putExtras(bundle)

                                sendIntentNotification(
                                    context,
                                    context.getString(R.string.emergency),
                                    context.getString(R.string.need_help),
                                    intent
                                )
                            }
                    }
                }
            }
            // Chain of listener instantiations
            activateListener(
                skills.hasVentolin,
                context.getString(R.string.asthma_med)
            )
            activateListener(
                skills.isMedicalPro,
                context.getString(R.string.med_pro)
            )
            activateListener(
                skills.hasEpipen,
                context.getString(R.string.epipen)
            )
            activateListener(
                skills.hasInsulin,
                context.getString(R.string.Insulin)
            )
            activateListener(
                skills.hasFirstAidKit,
                context.getString(R.string.first_aid_kit)
            )
            activateListener(skills.knowsCPR, context.getString(R.string.cpr))
        }
    }
}