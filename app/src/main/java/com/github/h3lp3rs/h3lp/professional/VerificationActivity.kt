package com.github.h3lp3rs.h3lp.professional

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.signin.GoogleSignInAdapter
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class VerificationActivity : AppCompatActivity() {

    companion object{
        var imgUri : Uri? = null
        val db = Databases.databaseOf(Databases.PRO_USERS)
        var storageRef = FirebaseStorage.getInstance().getReference("uploads")
        var currentUserId = SignInActivity.userUid.toString()
        var currentUserName = GoogleSignInAdapter.auth.currentUser?.displayName.toString()
    }

    private lateinit var chooseImgButton : Button
    private lateinit var uploadButton : Button
    private lateinit var fileName : EditText
    private lateinit var img : ImageView
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        chooseImgButton = findViewById(R.id.button_choose_img)
        uploadButton = findViewById(R.id.button_upload)
        fileName = findViewById(R.id.edit_file_name)
        img = findViewById(R.id.file)
        progressBar = findViewById(R.id.progress_bar)


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
                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.progress = 0
                }, 5000)

                val proUser = ProUser(id = currentUserId, name = currentUserName, proofName = fileName.text.toString().trim(), proofUri = it.uploadSessionUri.toString())
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