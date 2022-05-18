package com.github.h3lp3rs.h3lp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.dataclasses.Rating
import com.github.h3lp3rs.h3lp.signin.SignInActivity

class RatingActivity : AppCompatActivity() {
    private lateinit var ratingBar: RatingBar
    private lateinit var value: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        ratingBar = findViewById(R.id.rating_bar)
        value = findViewById(R.id.value)
        value.text = "Value: " + ratingBar.rating

        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener{ _, rating, _ ->
                value.text = "Value: $rating"
            }

        findViewById<Button>(R.id.send_feedback_button).setOnClickListener { sendFeedback() }

    }

    private fun sendFeedback(){
        val comment = findViewById<EditText>(R.id.comment).text.toString()
        val rating = Rating(ratingBar.rating, comment)
        SignInActivity.getName()
            ?.let { Databases.databaseOf(Databases.RATINGS).setObject(it,Rating::class.java,rating) }
    }
}


