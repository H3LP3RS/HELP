package com.github.h3lp3rs.h3lp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import android.widget.CheckBox
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.SignInActivity
import com.github.h3lp3rs.h3lp.listeners.SwipeListener
import com.github.h3lp3rs.h3lp.listeners.SwipeListener.Companion.SlideDirection.*
import com.github.h3lp3rs.h3lp.listeners.SwipeListener.Companion.swipeToNextActivity
import com.github.h3lp3rs.h3lp.preferences.Preferences
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.Files.*
import android.text.method.LinkMovementMethod

import android.text.TextUtils

import android.text.Spanned

import android.text.SpannableString

import android.text.TextPaint

import android.text.style.ClickableSpan
import com.github.h3lp3rs.h3lp.ORIGIN
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.USER_AGREE

/**
 * Class representing the third page of the app presentation
 * The purpose of this activity is to explain what H3LP ought not to be confused with
 */
class PresIrrelevantActivity : AppCompatActivity() {
    /**
     * Creates the third presentation page activity
     * Nothing should be done when a click is detected, this is handled by the swipe listener
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation_irrelevant)
        // 1. Set right box tick
        val checkBox = findViewById<View>(R.id.pres3_checkBox) as CheckBox
        if(Preferences(PRESENTATION, this).getBoolOrDefault(USER_AGREE, false)) {
            checkBox.isChecked = true
        }
        // 2. Add clickable text using the code of:
        // (https://stackoverflow.com/questions/8184597/how-do-i-make-a-portion-of-a-checkboxs-text-clickable)
        addClickableText(Intent(this, ToSActivity::class.java), checkBox)
        // 3. Set correct swipe listeners
        val gestureDetector = GestureDetector(this, SwipeListener(
            swipeToNextActivity(this, RIGHT, PresRelevantActivity::class.java, intent.getStringExtra(ORIGIN)), {}, {}, {}))
        findViewById<View>(R.id.pres3_textView5).setOnTouchListener { view, event ->
            view.performClick()
            gestureDetector.onTouchEvent(event)
        }
    }

    /**
     * Function called when the user presses the approval button
     */
    fun sendApproval(view: View) {
        val checkBox = findViewById<View>(R.id.pres3_checkBox) as CheckBox
        if(checkBox.isChecked) {
            Preferences(PRESENTATION, this).setBool(USER_AGREE, true)
            val i = Intent(this, findDest(intent.getStringExtra(ORIGIN)))
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun findDest(name: String?): Class<*>? {
        return when (name) {
            SignInActivity::class.qualifiedName -> {
                SignInActivity::class.java
            }
            MainPageActivity::class.qualifiedName -> {
                MainPageActivity::class.java
            }
            else -> null
        }
    }

    private fun addClickableText(tosIntent: Intent, checkBox: CheckBox) {
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Prevent CheckBox state from being toggled when link is clicked
                widget.cancelPendingInputEvents()
                // Open TOS activity
                startActivity(tosIntent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        val linkText = SpannableString(getString(R.string.clickable_ToS_text))
        linkText.setSpan(clickableSpan, 0, linkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val cs = TextUtils.expandTemplate(getString(R.string.accept_ToS), linkText)
        checkBox.text = cs
        checkBox.movementMethod = LinkMovementMethod.getInstance()
    }
}