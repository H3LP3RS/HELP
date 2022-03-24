package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.google.android.material.navigation.NavigationView

import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.signin.ORIGIN
import androidx.appcompat.app.AlertDialog

const val EXTRA_NEARBY_UTILITIES = "nearby_utilities"

/**
 * Main page of the app
 */
class MainPageActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_closed)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_profile -> goToProfileActivity(findViewById(R.id.profile))
                R.id.nav_home -> findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(
                    GravityCompat.START
                )
                else -> {
                    true
                }
            }
            true
        }
        // Demo code
        addAlertNotification()
    }

    // Demo code
    private fun addAlertNotification() {
        val db = databaseOf(Databases.NEW_EMERGENCIES)
        db.addListener(getString(R.string.ventolin_db_key), String::class.java) {
            if(it.equals(getString(R.string.help))){
                db.setString(getString(R.string.ventolin_db_key),getString(R.string.nothing))
                sendNotification(getString(R.string.emergency),getString(R.string.need_help))
            }
        }
    }

    private fun sendNotification(textTitle: String,textContent:String){
        AlertDialog.Builder(this).setTitle(textTitle).setMessage(textContent).setIcon(R.drawable.notification_icon).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }


    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    /** Called when the user taps the cpr rate button */
    fun goToCprActivity(view: View) {
        goToActivity(CprRateActivity::class.java)
    }

    /** Called when the user taps the help page button */
    fun goToHelpParametersActivity(view: View) {
         goToActivity(HelpParametersActivity::class.java)
    }

    /**
     * Called when the user taps on the info button
     * Starts the presentation of the app
     */
    fun viewPresentation(view: View) {
        startActivity(Intent(this, PresArrivalActivity::class.java)
            .putExtra(ORIGIN, MainPageActivity::class.qualifiedName))
    }

    /** Called when the user taps the profile page button */
    fun goToProfileActivity(view: View) {
        goToActivity(MedicalCardActivity::class.java)
    }

    /** Called when the user taps the nearby hospitals button */
    fun goToNearbyHospitals(view: View) {
        goToNearbyUtilities(resources.getString(R.string.nearby_hospitals))
    }

    /** Called when the user taps the nearby pharmacies button */
    fun goToNearbyPharmacies(view: View) {
        goToNearbyUtilities(resources.getString(R.string.nearby_phamacies))
    }

    private fun goToNearbyUtilities(utility: String) {
        val intent = Intent(this, NearbyUtilitiesActivity::class.java).apply{
            putExtra(EXTRA_NEARBY_UTILITIES, utility)
        }
        startActivity(intent)
    }



}