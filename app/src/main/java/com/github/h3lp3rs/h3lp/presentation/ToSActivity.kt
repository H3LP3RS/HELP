package com.github.h3lp3rs.h3lp.presentation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.github.h3lp3rs.h3lp.R
import android.view.View

import android.widget.TextView
import androidx.annotation.RequiresApi

class ToSActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tos_activity)
        // Display html TOS page according to
        // (https://stackoverflow.com/questions/14335988/best-practice-to-show-big-text-data-in-an-android-view)
        val textView = findViewById<View>(R.id.tos_textView) as TextView
        textView.text = Html.fromHtml(getString(R.string.tos_text), Html.FROM_HTML_MODE_LEGACY)
    }
}