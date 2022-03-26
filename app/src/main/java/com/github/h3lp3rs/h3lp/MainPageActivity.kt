package com.github.h3lp3rs.h3lp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.google.android.material.navigation.NavigationView
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.signin.ORIGIN
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt


const val EXTRA_NEARBY_UTILITIES = "nearby_utilities"

// Elements of the list view
const val PROFILE = "Profile"
const val CPR_RATE = "CPR rate"
const val TUTORIAL = "Tutorial"
const val HOSPITALS = "Hospitals"
const val PHARMACIES = "Pharmacies"

/**
 * Main page of the app
 */
class MainPageActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {
    private lateinit var toggle: ActionBarDrawerToggle
    private var locPermissionDenied = false

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView

    // List of searchable elements
    private var searchBarElements: ArrayList<String> = ArrayList()

    // Adapter for the list view
    lateinit var adapter: ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        setUpDrawerLayout()

        searchView = findViewById(R.id.searchBar)
        listView = findViewById(R.id.listView)

        // List view is invisible in the beginning.
        listView.visibility = View.GONE

        setUpListViewItems()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, searchBarElements)
        listView.adapter = adapter

        setUpSearchView()

        if (checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
        startAppGuide()
    }
    // This is to be able to pass the color id directly to the setBackgroundColour function instead
    // of using the getResource function. The difference is that the id enables having transparent colors.
    @SuppressLint("ResourceAsColor")
    private fun startAppGuide(){

        val prefManager = PreferenceManager.getDefaultSharedPreferences(this)
        if(!prefManager.getBoolean("didShowPrompt",false )){
            MaterialTapTargetPrompt.Builder(this).setTarget(R.id.profile).setPrimaryText("TODO")
                .setSecondaryText("TODO").setBackButtonDismissEnabled(false).setBackgroundColour(
                    R.color.black).setPromptStateChangeListener{ _, state ->
                    if(state==MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state==MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED){
                        val prefEditor= prefManager.edit()
                        prefEditor.putBoolean("TODO",true)
                        prefEditor.apply()
                        //showButtonPrompt()
                    }} .show()
        }
    }



    /**
     * Add elements to the list view
     */
    private fun setUpListViewItems() {
        searchBarElements.add(PROFILE)
        searchBarElements.add(CPR_RATE)
        searchBarElements.add(TUTORIAL)
        searchBarElements.add(PHARMACIES)
        searchBarElements.add(HOSPITALS)
    }

    /**
     * Handles the search feature on the home page. The list view is only visible when characters
     * are entered in the search field. On text submit, an appropriate message is displayed and a
     * new activity is launched.
     */
    private fun setUpSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                if (searchBarElements.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    displayErrorMessageAfterSearch()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    // When the text field of the search bar is empty, the list is hidden
                    listView.visibility = View.GONE
                } else {
                    // When the text field of the search bar is non empty, show relevant elements
                    listView.visibility = View.VISIBLE
                }
                // Enable showing only relevant items
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

    /**
     * Sets up the drawer layout used for the side bar menu.
     */
    private fun setUpDrawerLayout() {
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
            }
            true
        }
        // Demo code
        // addAlertNotification()
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

    /**
     * Starts activity based on the entered element in the search field.
     */
    private fun findActivity(listItem: String, view: View) {
        when (listItem) {
            PROFILE -> goToProfileActivity(view)
            CPR_RATE -> goToCprActivity(view)
            TUTORIAL -> viewPresentation(view)
            HOSPITALS -> goToNearbyHospitals(view)
            PHARMACIES -> goToNearbyPharmacies(view)

        }
    }

    /**
     * Displays a message using snackbar under the view
     * @param message message to display
     * @param view the view under which the message is shown
     */
    private fun displayMessage(message: String, view: View) {
        Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_SHORT).setAnchorView(view)
            .show()
    }

    private fun displayErrorMessageAfterSearch() {
        val horizontalScrollView = findViewById<View>(R.id.horizontalScrollView)
        displayMessage(getString(R.string.matchNotFound), horizontalScrollView)
    }

    private fun displaySelectedItem(item: String) {
        val horizontalScrollView = findViewById<View>(R.id.horizontalScrollView)
        displayMessage("Selected item : $item", horizontalScrollView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
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
            Toast.makeText(this, resources.getString(R.string.no_permission), Toast.LENGTH_SHORT).show()
            locPermissionDenied = false

            // Go back to tutorial
            startActivity(
                Intent(this, PresArrivalActivity::class.java)
                    .putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
            )
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

    /** Called when the user taps the first aid tips button */
    fun goToFirstAid(view: View) {
        goToActivity(FirstAidActivity::class.java)
    }

    private fun goToNearbyUtilities(utility: String) {
        val intent = Intent(this, NearbyUtilitiesActivity::class.java).apply {
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