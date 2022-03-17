package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity



class MainPageActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    /*
    val drawerLayout: DrawerLayout=findViewById<DrawerLayout>(R.layout.activity_main_page)
    val navigationView:NavigationView=findViewById(R.id.nav_view)
    val toolBar:ImageButton=findViewById(R.id.menuButton)
    val drawerToggle by lazy{
        ActionBarDrawerToggle(this,findViewById(R.id.bar_layout),findViewById(R.id.toolbar),R.string.drawer_open,R.string.drawer_closed)
    }
    private var mDrawerToggle: ActionBarDrawerToggle?=null
    */

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        /*
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<DrawerLayout>(R.id.bar_layout).addDrawerListener(drawerToggle)
        mDrawerToggle= ActionBarDrawerToggle(this,findViewById<DrawerLayout>(R.id.drawer_layout),findViewById(R.id.toolbar),R.string.drawer_open,R.string.drawer_closed)
        mDrawerToggle!!.syncState()
        */

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
                    // Toast.makeText(applicationContext, "TODO", LENGTH_SHORT).show()
                    true
                }
            }
            true
        }

    }

    /*
        override fun onPostCreate(savedInstanceState: Bundle?) {
            super.onPostCreate(savedInstanceState)
            drawerToggle.syncState()
        }

        override fun onConfigurationChanged(newConfig: Configuration) {
            super.onConfigurationChanged(newConfig)
            drawerToggle.onConfigurationChanged(newConfig)
        }
    */

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
        goToActivity(PresArrivalActivity::class.java)
    }

    /** Called when the user taps the profile page button */
    fun goToProfileActivity(view: View) {
        goToActivity(MedicalCardAcivity::class.java)
    }
}