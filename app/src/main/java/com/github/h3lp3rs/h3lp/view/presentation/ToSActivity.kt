package com.github.h3lp3rs.h3lp.view.signin.presentation

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R

class ToSActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tos_activity)
        // Display html TOS page according to
        // (https://stackoverflow.com/questions/14335988/best-practice-to-show-big-text-data-in-an-android-view)
        val textView = findViewById<View>(R.id.tos_textView) as TextView
        textView.text = Html.fromHtml(getString(R.string.tos_text), Html.FROM_HTML_MODE_LEGACY)
    }
}