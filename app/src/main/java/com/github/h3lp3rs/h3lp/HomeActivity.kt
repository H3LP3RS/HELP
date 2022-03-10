package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val signOutButton = findViewById<Button>(R.id.signOutButton)
        val name = findViewById<TextView>(R.id.name)
        val mail = findViewById<TextView>(R.id.mail)

        val signInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if(signInAccount != null){
            name.setText(signInAccount.displayName)
            mail.setText(signInAccount.email)
        }

        signOutButton.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}