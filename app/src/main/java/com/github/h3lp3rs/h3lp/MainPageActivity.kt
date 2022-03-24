package com.github.h3lp3rs.h3lp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.signin.ORIGIN

const val EXTRA_NEARBY_UTILITIES = "nearby_utilities"

/**
 * Main page of the app
 */
class MainPageActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {
    private lateinit var toggle: ActionBarDrawerToggle
    private var locPermissionDenied = false

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

        if (checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
            // Do nothing yet
        } else {
            // Permission was denied.
            // Display the missing permission error dialog when the fragments resume.
            locPermissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (locPermissionDenied) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            locPermissionDenied = false

            // Go back to tutorial
            startActivity(Intent(this, PresArrivalActivity::class.java)
                .putExtra(ORIGIN, MainPageActivity::class.qualifiedName))
        }
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

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}