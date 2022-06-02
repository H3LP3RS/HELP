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
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import kotlinx.android.synthetic.main.activity_forum_answers.*
import kotlinx.android.synthetic.main.activity_rating.*

class RatingActivity : AppCompatActivity() {
    private lateinit var ratingBar: RatingBar
    // the number of full stars in the rating bar
    private lateinit var ratingValue: TextView
    private val prefix = "Value: "

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        ratingBar = findViewById(R.id.rating_bar)
        ratingValue = findViewById(R.id.value)
        ratingValue.text = prefix + ratingBar.rating

        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                ratingValue.text = "$prefix $rating"
            }

        findViewById<Button>(R.id.send_feedback_button).setOnClickListener { sendFeedback() }
    }

    /**
     * Stores the user's feedback in the database
     */
    private fun sendFeedback() {
        val feedbackComment = comment.text.toString()
        val rating = Rating(ratingBar.rating, feedbackComment)
        userUid
            ?.let {
                Databases.databaseOf(Databases.RATINGS).setObject(it, Rating::class.java, rating)
            }
       comment.text.clear()
    }
}

