package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.signin.ORIGIN
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

const val EXTRA_NEARBY_UTILITIES = "nearby_utilities"

//
const val PROFILE = "Profile"
const val CPR_RATE = "CPR rate"
const val TUTORIAL = "Tutorial"

/**
 * Main page of the app
 */
class MainPageActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView

    //List of searchable elements
    private var searchBarElements: ArrayList<String> = ArrayList()

    //Adapter for the list view
    lateinit var adapter: ArrayAdapter<*>


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        //Set up the drawer layout
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

        searchView = findViewById(R.id.searchBar)
        listView = findViewById(R.id.listView)

        //List view is invisible in the beginning
        listView.visibility = View.GONE

        //Add elements to the list view
        searchBarElements.add(PROFILE)
        searchBarElements.add(CPR_RATE)
        searchBarElements.add(TUTORIAL)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, searchBarElements)
        listView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                if (searchBarElements.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    displayErrorMessage()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    //when the text field of the search bar is empty, the list is hidden
                    listView.visibility = View.GONE
                } else {
                    //when the text field of the search bar is non empty, show relevant elements
                    listView.visibility = View.VISIBLE
                }
                //enable showing filtered elements
                adapter.filter.filter(newText)
                return false
            }
        })

        listView.setOnItemClickListener { _, view, position, _ ->

            val listItem = listView.getItemAtPosition(position).toString()
            displaySelectedItem(listItem)
            findActivity(listItem, view)

        }

    }

    private fun findActivity(listItem: String, view: View) {
        when (listItem) {
            PROFILE -> goToProfileActivity(view)
            CPR_RATE -> goToCprActivity(view)
            TUTORIAL -> viewPresentation(view)
        }
    }

    /**
     * Displays a message using snackbar under the horizontal scroll view
     * @param message message to display
     */
    private fun displayMessage(message: String) {
        val fab = findViewById<View>(R.id.horizontalScrollView)
        Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_SHORT).setAnchorView(fab).show()
    }

    private fun displayErrorMessage(){
        displayMessage(getString(R.string.MatchNotFound))
    }

    private fun displaySelectedItem(item:String){
        displayMessage("Selected item : $item")
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
        startActivity(
            Intent(this, PresArrivalActivity::class.java)
                .putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
        )
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
        val intent = Intent(this, NearbyUtilitiesActivity::class.java).apply {
            putExtra(EXTRA_NEARBY_UTILITIES, utility)
        }
        startActivity(intent)
    }
}