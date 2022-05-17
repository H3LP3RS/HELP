package com.github.h3lp3rs.h3lp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.notification.EmergencyListener
import com.github.h3lp3rs.h3lp.database.Databases.PRO_USERS
import com.github.h3lp3rs.h3lp.forum.FireForum
import com.github.h3lp3rs.h3lp.forum.ForumPostsActivity
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.professional.ProMainActivity
import com.github.h3lp3rs.h3lp.professional.ProUser
import com.github.h3lp3rs.h3lp.professional.VerificationActivity
import com.github.h3lp3rs.h3lp.signin.SignIn
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.storage.Storages.USER_COOKIE
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal


const val EXTRA_NEARBY_UTILITIES = "nearby_utilities"
const val GUIDE_KEY = "didShowGuide"

// Elements of the list view
const val PROFILE = "Profile"
const val CPR_RATE = "CPR rate"
const val TUTORIAL = "Tutorial"
const val HOSPITALS = "Hospitals"
const val PHARMACIES = "Pharmacies"

private val mainPageButton = listOf(
    MainPageButton(R.id.button_profile, false),
    MainPageButton(R.id.button_tutorial, false),
    MainPageButton(R.id.button_my_skills, false),
    MainPageButton(R.id.button_hospital, true),
    MainPageButton(R.id.button_defibrillator, true),
    MainPageButton(R.id.button_pharmacy, true),
    MainPageButton(R.id.button_first_aid, true),
    MainPageButton(R.id.button_cpr, true)
)

private val buttonsGuidePrompts = mapOf(
    R.id.button_tutorial to R.string.tuto_guide_prompt,
    R.id.button_profile to R.string.profile_guide_prompt,
    R.id.button_my_skills to R.string.skills_guide_prompt,
    R.id.button_hospital to R.string.hospitals_guide_prompt,
    R.id.button_defibrillator to R.string.defibrillators_guide_prompt,
    R.id.button_pharmacy to R.string.pharmacies_guide_prompt,
    R.id.button_first_aid to R.string.first_aid_guide_prompt,
    R.id.button_cpr to R.string.cpr_guide_prompt
)
val numberOfButtons = mainPageButton.size

/**
 * Main page of the app
 */
class MainPageActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {
    private lateinit var toggle : ActionBarDrawerToggle
    private var locPermissionDenied = false

    private lateinit var searchView : SearchView
    private lateinit var listView : ListView
    private lateinit var storage : LocalStorage

    // List of searchable elements
    private var searchBarElements : ArrayList<String> = ArrayList()

    // Adapter for the list view
    lateinit var adapter : ArrayAdapter<*>

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        // Load the storage
        storage = storageOf(USER_COOKIE)

        // Set the toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

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
            showExplanationAndRequestPermissions()
        }

        // Start listening to forum posts
        FireForum(emptyList()).sendIntentNotificationOnNewPosts(
            globalContext, ForumPostsActivity::class.java
        )
        EmergencyListener.activateListeners()

        startAppGuide()
    }

    /**
     * Opens a popup explaining why the app needs permission with a nice image.
     * Once the user closes the popup, the formal system permission is asked.
     */
    private fun showExplanationAndRequestPermissions() {
        val dialog = Dialog(this)
        val emergencyCallPopup = layoutInflater.inflate(R.layout.localization_permission_popup, null)

        dialog.setCancelable(false)
        dialog.setContentView(emergencyCallPopup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.create()

        // pass button
        emergencyCallPopup.findViewById<Button>(R.id.accept_permission_popup_button).setOnClickListener {
            dialog.dismiss()
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        dialog.show()
    }

    /**
     * When the user does no longer see the activity
     */
    override fun onStop() {
        super.onStop()
        storage.push()
    }

    override fun onResume() {
        super.onResume()
        // This removes the focus from the search bar when the user goes back to the main page
        // after clicking on an item from the list view.
        searchView.clearFocus()
    }

    /**
     * Starts the application guide.
     */
    private fun startAppGuide() {
        if (!storage.getBoolOrDefault(GUIDE_KEY, false)) {
            storage.setBoolean(GUIDE_KEY, true)
            // Starts the guide of main page buttons. Once it finishes, it shows the
            // prompt for the search bar by executing the showSearchBarPrompt.
            showButtonPrompt(mainPageButton, buttonsGuidePrompts) { showSearchBarPrompt() }
        }
    }

    // This is to be able to pass the color id directly to the setBackgroundColour function instead
    // of using the getResource function. The difference is that the id enables having transparent
    // colors which is more aesthetically pleasing.
    @SuppressLint("ResourceAsColor")
    /**
     * Recursive function that handles showing the prompt for the buttons in the list buttons.
     * If the list is empty a call to showNextGuide is made.
     * @param buttons The list of the buttons' ids for which a prompt has to be shown.
     * @param idToPrompt Map between a button id and the corresponding text to be shown.
     * @param showNextGuide Called once a prompt is shown for all the buttons in the list.
     */
    private fun showButtonPrompt(
        buttons : List<MainPageButton>, idToPrompt : Map<Int, Int>, showNextGuide : () -> Unit
    ) {
        if (buttons.isEmpty()) return showNextGuide()
        // We show the prompt for the head of the list.
        val button = buttons[0]
        val buttonId = button.getButtonId()

        // If the button is in the scroll view, it may be necessary to scroll to the button.
        if (button.isInScrollView()) scrollTo(buttonId)

        // The default shape of the highlighter is circular which is perfect for the buttons,
        // thus we do not change it. The default text color is white.
        idToPrompt[buttonId]?.let {
            MaterialTapTargetPrompt.Builder(this)
                // Sets which button to highlight
                .setTarget(buttonId).setPrimaryText(R.string.guide_primary_prompt)
                .setSecondaryText(it).setBackButtonDismissEnabled(false).setBackgroundColour(
                    R.color.black
                ).setPromptStateChangeListener { _, state ->
                    // If the user clicks anywhere on the screen, we move to the next button
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                        // Recursive call by removing the head of the list for which the prompt
                        // has already been shown
                        showButtonPrompt(buttons.drop(1), idToPrompt, showNextGuide)
                    }
                }.show()
        }
    }

    private fun scrollTo(buttonId : Int) {
        val sv = findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
        sv.requestChildFocus(findViewById(buttonId), findViewById(buttonId))
    }

    @SuppressLint("ResourceAsColor")
    private fun showSearchBarPrompt() {
        MaterialTapTargetPrompt.Builder(this).setTarget(findViewById(R.id.searchBar))
            .setPrimaryText(R.string.guide_primary_prompt)
            .setSecondaryText(R.string.guide_search_bar_prompt).setBackButtonDismissEnabled(true)
            // Since the search bar is rectangular, it is best to use a rectangular highlighter.
            .setPromptBackground(
                RectanglePromptBackground()
            ).setPromptFocal(RectanglePromptFocal()).setBackgroundColour(
                R.color.black
            ).setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    // Displays a message to signal the end of the guide.
                    displayMessage(
                        getString(R.string.AppGuideFinished),
                        findViewById(R.id.horizontalScrollView)
                    )
                    // Clears the focus from the search view so as to not open the keyboard.
                    searchView.clearFocus()
                }
            }.show()
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
            override fun onQueryTextSubmit(query : String) : Boolean {
                if (searchBarElements.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    displayErrorMessageAfterSearch()
                }
                return false
            }

            override fun onQueryTextChange(newText : String) : Boolean {
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
        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_closed)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_profile -> goToProfileActivity(findViewById(R.id.button_profile))
                R.id.nav_home -> {
                    findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(
                        GravityCompat.START
                    )
                    // Only to deselect the home button
                    goToActivity(MainPageActivity::class.java)
                }
                R.id.nav_settings -> goToActivity(SettingsActivity::class.java)
                R.id.nav_about_us -> goToActivity(PresArrivalActivity::class.java)
                R.id.nav_logout -> {
                    SignIn.get().signOut()
                    goToActivity(SignInActivity::class.java)
                }
                R.id.nav_rate_us -> goToActivity(RatingActivity::class.java)
            }
            true
        }
    }

    /**
     * Starts activity based on the entered element in the search field.
     */
    private fun findActivity(listItem : String, view : View) {
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
    private fun displayMessage(message : String, view : View) {
        Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_SHORT).setAnchorView(view)
            .show()
    }

    private fun displayErrorMessageAfterSearch() {
        val horizontalScrollView = findViewById<View>(R.id.horizontalScrollView)
        displayMessage(getString(R.string.match_not_found), horizontalScrollView)
    }

    private fun displaySelectedItem(item : String) {
        val horizontalScrollView = findViewById<View>(R.id.horizontalScrollView)
        displayMessage("Selected item : $item", horizontalScrollView)
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        if(item.itemId == R.id.button_tutorial){
            viewPresentation(findViewById<View>(android.R.id.content).rootView)
        }
        else if(item.itemId == R.id.toolbar_settings){
            goToSettings(findViewById<View>(android.R.id.content))
        }

        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode : Int, permissions : Array<String>, grantResults : IntArray
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
            Toast.makeText(this, resources.getString(R.string.no_permission), Toast.LENGTH_SHORT)
                .show()
            locPermissionDenied = false

            // Go back to tutorial
            goToActivity(PresArrivalActivity::class.java)
        }
    }

    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.menu_toolbar,menu)
        return true
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName : Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    /** Called when the user taps the cpr rate button */
    fun goToCprActivity(view : View) {
        goToActivity(CprRateActivity::class.java)
    }

    /** Called when the user taps the help page button */
    fun goToHelpParametersActivity(view : View) {
        goToActivity(HelpeeSelectionActivity::class.java)
    }

    /**
     * Called when the user taps on the info button
     * Starts the presentation of the app
     */
    private fun viewPresentation(view : View) {
        goToActivity(PresArrivalActivity::class.java)
    }

    /** Called when the user taps the profile page button */
    fun goToProfileActivity(view : View) {
        goToActivity(MedicalCardActivity::class.java)
    }

    /** Called when the user taps the my skills button */
    fun goToMySkillsActivity(view: View) {
        goToActivity(MySkillsActivity::class.java)
    }

    /** Called when the user taps the nearby hospitals button */
    fun goToNearbyHospitals(view : View) {
        goToNearbyUtilities(resources.getString(R.string.nearby_hospitals))
    }

    /** Called when the user taps the nearby pharmacies button */
    fun goToNearbyPharmacies(view : View) {
        goToNearbyUtilities(resources.getString(R.string.nearby_phamacies))
    }

    /** Called when the user taps the first aid tips button */
    fun goToFirstAid(view : View) {
        goToActivity(FirstAidActivity::class.java)
    }

    /** Called when the user taps the first aid tips button */
    fun goToSettings(view: View) {
        goToActivity(SettingsActivity::class.java)
    }

    /** Called when the user taps the professional portal  button */
    fun goToProfessionalPortal(view : View) {
        val db = databaseOf(PRO_USERS)
        db.getObject(SignInActivity.userUid.toString(), ProUser::class.java).handle { _, err ->
            if(err != null){
                // If there is no proof of the status of the current user in the database, launch the verification process
                goToActivity(VerificationActivity::class.java)
                return@handle
            }
            // Otherwise, redirect to the professional main page
            goToActivity(ProMainActivity::class.java)
        }
    }

    private fun goToNearbyUtilities(utility : String) {
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

private class MainPageButton(private val buttonId : Int, private val isInScrollView : Boolean) {

    fun isInScrollView() : Boolean {
        return isInScrollView
    }

    fun getButtonId() : Int {
        return buttonId
    }

}