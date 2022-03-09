package com.github.h3lp3rs.h3lp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast


class AwaitHelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await_help)

        val bundle = this.intent.extras
        val array = bundle!!.getStringArrayList(EXTRA_NEEDED_MEDICATION)

        // To be removed once the page is implemented:
        val displayText = "Selected: $array"
        val text = findViewById<TextView>(R.id.selected_items_text).apply{
            text = displayText
        }

        //TODO Launch helper search
        // a: Retrieve nearby users from online database            => loading bar (up to 50%
        // b: send notification to users                            => loading bar (50-100%)
        // c: wait for users                                        => spinning logo
        // d: show users who accepted the help request on the map

        //TODO Pop up suggesting to call emergencies, explaining help is not assured

    }
}