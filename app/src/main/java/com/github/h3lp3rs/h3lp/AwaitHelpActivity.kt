package com.github.h3lp3rs.h3lp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
        val text = findViewById<TextView>(R.id.selected_items_text).apply {
            text = displayText
        }
        */

        // TODO Launch helper search
        // a: Retrieve nearby users from online database            => loading bar (up to 50%
        // b: send notification to users                            => loading bar (50-100%)
        // c: wait for users                                        => spinning logo
        // d: show users who accepted the help request on the map

        // TODO Pop up suggesting to call emergencies, explaining help is not assured
        val builder = AlertDialog.Builder(this)
        val emergencyCallPopup = layoutInflater.inflate(R.layout.call_emergencies_popup, null)

        builder.setCancelable(false)
        builder.setView(emergencyCallPopup)

        val alertDialog = builder.create()
        alertDialog.show()
    }
}