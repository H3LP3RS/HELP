package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf

/**
 * Activity during which the user waits for help from other user.
 */
class AwaitHelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await_help)

        val bundle = this.intent.extras
        val array = bundle!!.getStringArrayList(EXTRA_NEEDED_MEDICATION)

        // To be removed once the page is implemented:
        val displayText = "Selected: $array"
        val db = databaseOf(NEW_EMERGENCIES)
        // Demo code
        /*
        db.clearListeners(getString(R.string.ventolin_db_key)) // Avoid being autocalled (for now, we'll need a ds for that later)
        db.setString(getString(R.string.ventolin_db_key), getString(R.string.help))
        */

        // TODO Launch helper search
        // a: Retrieve nearby users from online database            => loading bar (up to 50%
        // b: send notification to users                            => loading bar (50-100%)
        // c: wait for users                                        => spinning logo
        // d: show users who accepted the help request on the map

//        if(?){
//            showCallPopup()
//        }


        //TODO add listener on database so that we replace the loading bar by the number of
        // users coming to help

        //TODO display guides according to emergency required

        //TODO offer to increase map size, display coming users and potentially nearby utilities
    }

    private fun showCallPopup(){
        val builder = AlertDialog.Builder(this)
        val emergencyCallPopup = layoutInflater.inflate(R.layout.call_emergencies_popup, null)

        builder.setCancelable(false)
        builder.setView(emergencyCallPopup)

        val alertDialog = builder.create()

        // pass button
        emergencyCallPopup.findViewById<Button>(R.id.close_call_popup_button).setOnClickListener {
            alertDialog.cancel()
        }

        // call button
//        emergencyCallPopup.findViewById<Button>(R.id.open_call_popup_button).setOnClickListener {
//            TODO()
//            updateCoordinates()
//            val emergencyNumber =
//                LocalEmergencyCaller.getLocalEmergencyNumber(
//                    userLocation?.longitude,
//                    userLocation?.latitude,
//                    this
//                )
//
//            val dial = "tel:$emergencyNumber"
//            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
//        }

        alertDialog.show()
    }

    companion object {

    }
}