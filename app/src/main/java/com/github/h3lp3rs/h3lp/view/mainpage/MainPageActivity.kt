package com.github.h3lp3rs.h3lp.view.mainpage

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
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.PRO_USERS
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory
import com.github.h3lp3rs.h3lp.model.notifications.EmergencyListener
import com.github.h3lp3rs.h3lp.model.professional.ProUser
import com.github.h3lp3rs.h3lp.model.signin.SignIn
import com.github.h3lp3rs.h3lp.model.storage.LocalStorage
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.model.storage.Storages.SIGN_IN
import com.github.h3lp3rs.h3lp.model.storage.Storages.USER_COOKIE
import com.github.h3lp3rs.h3lp.view.firstaid.FirstAidActivity
import com.github.h3lp3rs.h3lp.view.forum.ForumCategoriesActivity
import com.github.h3lp3rs.h3lp.view.forum.ForumPostsActivity
import com.github.h3lp3rs.h3lp.view.helprequest.helpee.HelpeeSelectionActivity
import com.github.h3lp3rs.h3lp.view.map.NearbyUtilitiesActivity
import com.github.h3lp3rs.h3lp.view.professional.ProMainActivity
import com.github.h3lp3rs.h3lp.view.professional.VerificationActivity
import com.github.h3lp3rs.h3lp.view.profile.MedicalCardActivity
import com.github.h3lp3rs.h3lp.view.profile.MySkillsActivity
import com.github.h3lp3rs.h3lp.view.profile.SettingsActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.getUid
import com.github.h3lp3rs.h3lp.view.signin.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.view.utils.ActivityUtils.goToActivity
import com.github.h3lp3rs.h3lp.view.utils.ActivityUtils.goToMainPage
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main_page.*
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

private val mainPageButtons = listOf(
    MainPageButton(R.id.button_profile, false, R.string.profile_guide_prompt),
    MainPageButton(R.id.button_tutorial, false, R.string.tuto_guide_prompt),
    MainPageButton(R.id.button_my_skills, false, R.string.skills_guide_prompt),
    MainPageButton(R.id.button_hospital, true, R.string.hospitals_guide_prompt),
    MainPageButton(R.id.button_defibrillator, true, R.string.defibrillators_guide_prompt),
    MainPageButton(R.id.button_pharmacy, true, R.string.pharmacies_guide_prompt),
    MainPageButton(R.id.button_first_aid, true, R.string.first_aid_guide_prompt),
    MainPageButton(R.id.button_cpr, true, R.string.cpr_guide_prompt),
    MainPageButton(R.id.button_forum, true, R.string.forum_guide_prompt)
)

/**
 * Main page of the app
 */
class MainPageActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {
    private lateinit var toggle: ActionBarDrawerToggle
    private var locPermissionDenied = false
    private var isGuideEnabled = false

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var storage: LocalStorage

    // List of searchable elements
    private var searchBarElements: ArrayList<String> = ArrayList()

    // Adapter for the list view
    lateinit var adapter: ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // Load the storage
        storage = storageOf(USER_COOKIE, applicationContext)

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
        ForumCategory.root(applicationContext).sendIntentNotificationOnNewPosts(
            applicationContext, ForumPostsActivity::class.java
        )

        EmergencyListener.activateListeners(applicationContext)

        startAppGuide()
    }

    /**
     * Disables the back button on the main page. This is the simplest solution to avoid breaking
     * the sign in tests.
     */
    override fun onBackPressed() {}

    /**
     * Opens a popup asking the user to sign in to continue.
     */
    @SuppressLint("InflateParams")
    private fun showSignInPopUp() {
        val dialog = Dialog(this)
        val signInPopup =
            layoutInflater.inflate(R.layout.sign_in_required_pop_up, null)

        dialog.setCancelable(false)
        dialog.setContentView(signInPopup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.create()

        // Cancel button
        signInPopup.findViewById<Button>(R.id.close_popup_button)
            .setOnClickListener {
                dialog.dismiss()
            }
        // Sign in button
        signInPopup.findViewById<Button>(R.id.sign_in_popup_button)
            .setOnClickListener {
                dialog.dismiss()
                goToActivity(SignInActivity::class.java)
            }

        dialog.show()
    }

    /**
     * Opens a popup explaining why the app needs permission with a nice image.
     * Once the user closes the popup, the formal system permission is asked.
     */
    @SuppressLint("InflateParams")
    private fun showExplanationAndRequestPermissions() {
        val dialog = Dialog(this)
        val emergencyCallPopup =
            layoutInflater.inflate(R.layout.localization_permission_popup, null)

        dialog.setCancelable(false)
        dialog.setContentView(emergencyCallPopup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.create()

        // Pass button
        emergencyCallPopup.findViewById<Button>(R.id.accept_permission_popup_button)
            .setOnClickListener {
                dialog.dismiss()
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }

        dialog.show()
    }

    /**
     * When the user no longer sees the activity
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
        val signInStorage = storageOf(SIGN_IN, applicationContext)
        if (!signInStorage.getBoolOrDefault(GUIDE_KEY, false)) {
            signInStorage.setBoolean(GUIDE_KEY, true)

            // Disable buttons so that the guide can be seen to completion
            isGuideEnabled = true

            // Starts the guide of main page buttons. Once it finishes, it shows the
            // prompt for the search bar by executing the showSearchBarPrompt.
            showButtonPrompt(mainPageButtons) { showSearchBarPrompt() }

            // Re-enable the buttons
            isGuideEnabled = false
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
     * @param showNextGuide Called once a prompt is shown for all the buttons in the list.
     */
    private fun showButtonPrompt(
        buttons: List<MainPageButton>, showNextGuide: () -> Unit
    ) {
        if (buttons.isEmpty()) return showNextGuide()
        // We show the prompt for the head of the list.
        val button = buttons[0]
        val buttonId = button.buttonId

        // If the button is in the scroll view, it may be necessary to scroll to the button.
        if (button.isInScrollView) scrollTo(buttonId)

        // The default shape of the highlighter is circular which is perfect for the buttons,
        // thus we do not change it. The default text color is white.
        MaterialTapTargetPrompt.Builder(this)
            // Sets which button to highlight
            .setTarget(buttonId).setPrimaryText(R.string.guide_primary_prompt)
            .setSecondaryText(button.promptTextId).setBackButtonDismissEnabled(false)
            .setBackgroundColour(
                R.color.black
            ).setPromptStateChangeListener { _, state ->
                // If the user clicks anywhere on the screen, we move to the next button
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    // Recursive call by removing the head of the list for which the prompt
                    // has already been shown
                    showButtonPrompt(buttons.drop(1), showNextGuide)
                }
            }.show()
    }

    private fun scrollTo(buttonId: Int) {
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
                R.id.nav_profile -> goToButtonActivity(findViewById(R.id.button_profile))
                R.id.nav_home -> {
                    findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(
                        GravityCompat.START
                    )
                    // Only to deselect the home button
                    goToMainPage()
                }
                R.id.nav_settings -> goToActivity(SettingsActivity::class.java)
                R.id.nav_about_us -> goToActivity(PresArrivalActivity::class.java)
                R.id.nav_logout -> {
                    val userSignIn = storageOf(SIGN_IN, applicationContext) // Fetch from storage
                    userSignIn.setBoolean(getString(R.string.KEY_USER_SIGNED_IN), false)
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
    private fun findActivity(listItem: String, view: View) {
        when (listItem) {
            PROFILE -> goToButtonActivity(button_profile)
            CPR_RATE -> goToButtonActivity(button_cpr)
            TUTORIAL -> goToButtonActivity(findViewById(R.id.button_tutorial))
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
        displayMessage(getString(R.string.match_not_found), horizontalScrollView)
    }

    private fun displaySelectedItem(item: String) {
        val horizontalScrollView = findViewById<View>(R.id.horizontalScrollView)
        displayMessage("Selected item : $item", horizontalScrollView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.button_tutorial || item.itemId == R.id.toolbar_settings) {
            goToButtonActivity(findViewById(item.itemId))
        }

        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    /** Called when the user taps the help page button */
    fun goToHelpParametersActivity(view: View) {
        if (getUid() == null) {
            showSignInPopUp()
        } else {
            goToActivity(HelpeeSelectionActivity::class.java)
        }
    }

    /** Called when the user taps the nearby hospitals button */
    fun goToNearbyHospitals(view: View) {
        goToNearbyUtilities(resources.getString(R.string.nearby_hospitals))
    }

    /** Called when the user taps the nearby defibrillators button */
    fun goToNearbyDefibrillators(view: View) {
        goToNearbyUtilities(resources.getString(R.string.nearby_defibrillators))
    }

    /** Called when the user taps the nearby pharmacies button */
    fun goToNearbyPharmacies(view: View) {
        goToNearbyUtilities(resources.getString(R.string.nearby_phamacies))
    }


    /**
     * Called whenever a user clicks a button in the activity, launches the button's corresponding
     * activity, only used for buttons that directly launch an activity
     * @param view The button that was clicked
     */
    fun goToButtonActivity(view: View) {
        // If guide is showing, we disable the buttons
        if(!isGuideEnabled) {
            // If the view isn't one of the buttons, stays on the main page
            goToActivity(
                when (view.id) {
                    R.id.button_profile -> MedicalCardActivity::class.java
                    R.id.button_my_skills -> MySkillsActivity::class.java
                    R.id.button_first_aid -> FirstAidActivity::class.java
                    R.id.button_cpr -> CprRateActivity::class.java
                    R.id.HELP_button -> HelpeeSelectionActivity::class.java
                    R.id.button_forum -> ForumCategoriesActivity::class.java
                    R.id.button_tutorial -> PresArrivalActivity::class.java
                    R.id.toolbar_settings -> SettingsActivity::class.java
                    else -> {
                        MainPageActivity::class.java
                    }
                }
            )
        }
    }

    /** Called when the user taps the professional portal  button */
    fun goToProfessionalPortal(view: View) {
        if (getUid() == null) {
            showSignInPopUp()
        } else {
            val db = databaseOf(PRO_USERS, applicationContext)
            db.getObject(SignInActivity.userUid.toString(), ProUser::class.java).handle { _, err ->
                if (err != null) {
                    // If there is no proof of the status of the current user in the database, launch the verification process
                    goToActivity(VerificationActivity::class.java)
                    return@handle
                }
                // Otherwise, redirect to the professional main page
                goToActivity(ProMainActivity::class.java)
            }
        }
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

/**
 * Data class that represents the main page buttons and some information about them (the fact that
 * they are in the scroll view as well as the prompt that should appear for them in the guide) to be
 * able to directly get the information on that button
 * @param buttonId The button's id
 * @param isInScrollView If the button appears in the scroll view (the scrolling window in the middle
 * of the main page activity)
 * @param promptTextId The text that should be shown when the guide does an explanation for that
 * button
 */
private data class MainPageButton(
    val buttonId: Int,
    val isInScrollView: Boolean,
    val promptTextId: Int
)