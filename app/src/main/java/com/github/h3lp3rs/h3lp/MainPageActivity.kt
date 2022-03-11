package com.github.h3lp3rs.h3lp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class MainPageActivity : AppCompatActivity() {
    //@SuppressLint("ResourceType")
   // val drawerLayout: DrawerLayout=findViewById<DrawerLayout>(R.layout.activity_main_page)
    //val navigationView:NavigationView=findViewById(R.id.nav_view)
    //val toolBar:ImageButton=findViewById(R.id.menuButton)

   // @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?){
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
        goToActivity(PresentationActivity1::class.java)
    }

    /** Called when the user taps the profile page button */
    fun goToProfileActivity(view: View) {
        goToActivity(MedicalCardAcivity::class.java)
    }
}