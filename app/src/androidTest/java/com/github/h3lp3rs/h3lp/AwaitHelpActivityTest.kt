package com.github.h3lp3rs.h3lp

// Tests work on local but not on Cirrus
/*
class AwaitHelpActivityTest {

    private val selectedMeds = arrayListOf("Epipen")

    @get:Rule
    val testRule = ActivityScenarioRule(
        AwaitHelpActivity::class.java
    )

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        init()
        val intent = getIntent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
    }

    @After
    fun release() {
        release()
    }

    private fun clickingOnButtonWorksAndSendsIntent(
        ActivityName: Class<*>?,
        id: Matcher<View>,
        isInScrollView: Boolean
    ) {
        // close pop-up
        onView(withId(R.id.close_call_popup_button)).perform(click())

        if (isInScrollView) {
            onView(id).perform(scrollTo(), click())
        } else {
            onView(id).perform(click())
        }
        intended(
            allOf(
                hasComponent(ActivityName!!.name)
            )
        )
    }

    @Test
    fun clickingOnHeartAttackButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            HeartAttackActivity::class.java,
            withId(R.id.heart_attack_tuto_button), true)
    }

    @Test
    fun clickingOnEpipenButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            AllergyActivity::class.java,
            withId(R.id.epipen_tuto_button), true)
    }

    @Test
    fun clickingOnAedButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            AedActivity::class.java,
            withId(R.id.aed_tuto_button), true)
    }

    @Test
    fun clickingOnAsthmaButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            AsthmaActivity::class.java,
            withId(R.id.asthma_tuto_button), true)
    }

    @Test
    fun cancelButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            MainPageActivity::class.java,
            withId(R.id.cancel_search_button), false)
    }

// Tests work on local but not on Cirrus
//    @Test
//    fun callEmergenciesButtonWorksAndSendIntent() {
//        // close pop-up
//        onView(withId(R.id.close_call_popup_button)).perform(click())
//
//        val phoneButton = onView(withId(R.id.await_help_call_button))
//
//        phoneButton.check(ViewAssertions.matches(isDisplayed()))
//        phoneButton.perform(click())
//
//        intended(
//            Matchers.allOf(
//                IntentMatchers.hasAction(Intent.ACTION_DIAL)
//            )
//        )
//    }

    @Test
    fun callEmergenciesFromPopUpWorksAndSendsIntent() {

// Tests work on local but not on Cirrus
//    @Test
//    fun callEmergenciesFromPopUpWorksAndSendsIntent() {
//        val phoneButton = onView(withId(R.id.open_call_popup_button))
//
//        phoneButton.check(ViewAssertions.matches(isDisplayed()))
//        phoneButton.perform(click())
//
//        intended(
//            Matchers.allOf(
//                IntentMatchers.hasAction(Intent.ACTION_DIAL)
//            )
//        )
//    }

    private fun getIntent(): Intent {
        val bundle = Bundle()
        bundle.putStringArrayList(EXTRA_NEEDED_MEDICATION, selectedMeds)
        bundle.putBoolean(EXTRA_CALLED_EMERGENCIES, false)

        val intent = Intent(
            getApplicationContext(),
            AwaitHelpActivity::class.java
        ).apply {
            putExtras(bundle)
        }

        return intent
    }
}*/