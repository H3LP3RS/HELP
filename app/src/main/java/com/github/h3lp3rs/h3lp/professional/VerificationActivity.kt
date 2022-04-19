package com.github.h3lp3rs.h3lp.professional

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.signin.GoogleSignInAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class VerificationActivity : AppCompatActivity() {
    private var imgUri : Uri? = null
    private lateinit var chooseImgButton : Button
    private lateinit var uploadButton : Button
    private lateinit var fileName : EditText
    private lateinit var img : ImageView
    private lateinit var progressBar : ProgressBar
    private lateinit var storageRef : StorageReference
    private val db = Databases.databaseOf(Databases.NEW_EMERGENCIES) //TODO create prousers db

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        chooseImgButton = findViewById(R.id.button_choose_img)
        uploadButton = findViewById(R.id.button_upload)
        fileName = findViewById(R.id.edit_file_name)
        img = findViewById(R.id.file)
        progressBar = findViewById(R.id.progress_bar)
        storageRef = FirebaseStorage.getInstance().getReference("uploads")

        chooseImgButton.setOnClickListener{
            openFileChooser()
        }
        uploadButton.setOnClickListener{
            uploadFile()
        }

    }

    private fun openFileChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK && result.data != null && result.data!!.data != null){
            imgUri = result.data!!.data!!
            Picasso.with(this).load(imgUri).into(img)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }
    
    private fun uploadFile(){
        if(imgUri != null){
            val fileReference : StorageReference = storageRef.child(System.currentTimeMillis().toString() + "." + getFileExtension(
                imgUri!!
            ))
            fileReference.putFile(imgUri!!).addOnSuccessListener {
                val handler  = Handler()
                handler.postDelayed(Runnable { progressBar.progress = 0 }, 5000)

                val currentUser = GoogleSignInAdapter.auth.currentUser
                val id = currentUser?.uid.toString()
                val name = currentUser?.displayName.toString()

                val proUser = ProUser(id = id, name = name, proofName = fileName.text.toString().trim(), proofUri = it.uploadSessionUri.toString())
                db.setObject(proUser.id, ProUser::class.java, proUser)

                val intent = Intent(this, ProMainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener{
                Log.d("Hello","failed")
            }.addOnProgressListener {
                progressBar.progress = (100.0 * it.bytesTransferred / it.totalByteCount).toInt()
            }
        }
        else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

}