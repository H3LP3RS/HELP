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

/**
 * Activity to verify the professional status of the user before redirecting to the professional portal
 */
class VerificationActivity : AppCompatActivity() {

    // Some attributes are vars only for testing purposes
    companion object {
        // Uri of the image file = proof of status
        var imgUri: Uri? = null

        // Database representing the professional users authenticated
        val db = Databases.databaseOf(Databases.PRO_USERS)

        // Reference to the Firebase cloud storage
        var storageRef = FirebaseStorage.getInstance().getReference("uploads")

        // Type of the intent to launch when choosing an image from the device
        private const val INTENT_TYPE = "image/*"
        private const val UPLOAD_FAILURE_MSG = "Upload failed. Try again!"
        var currentUserId = SignInActivity.userUid.toString()
        var currentUserName = GoogleSignInAdapter.auth.currentUser?.displayName.toString()

    }

    // Layout components
    private lateinit var chooseImgButton: Button
    private lateinit var uploadButton: Button
    private lateinit var fileName: EditText
    private lateinit var img: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        chooseImgButton = findViewById(R.id.button_choose_img)
        uploadButton = findViewById(R.id.button_upload)
        fileName = findViewById(R.id.edit_file_name)
        img = findViewById(R.id.file)
        progressBar = findViewById(R.id.progress_bar)

        chooseImgButton.setOnClickListener {
            openFileChooser()
        }
        uploadButton.setOnClickListener {
            uploadFile()
        }
    }

    /**
     * Opens the image file chooser of the device inorder to select an image
     */
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = INTENT_TYPE
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    /**
     * Handles the intent result launched by the image file selection
     */
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data!!.data != null) {
                imgUri = result.data!!.data!!
                // Display the selected image
                Picasso.with(this).load(imgUri).into(img)
            }
        }

    /**
     * Retrieves the file extension
     *
     * @param uri Uri of the file
     * @return the extension of the file
     */
    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

    /**
     * Uploads the selected file into the cloud storage as well as the stores the current user in the authenticated users
     * database
     */
    private fun uploadFile() {
        if (imgUri != null) {
            // Creates a file in the cloud storage with the current time as name (Sufficient to avoid collision but can be redesigned in the future )
            val fileReference: StorageReference = storageRef.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(
                    imgUri!!
                )
            )
            // Stores the file the cloud storage
            fileReference.putFile(imgUri!!).addOnSuccessListener {
                // A delay to be able to see the progress bar at 100% before redirection
                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.progress = 0
                }, 5000)

                val proUser = ProUser(
                    id = currentUserId,
                    name = currentUserName,
                    proofName = fileName.text.toString().trim(),
                    proofUri = it.uploadSessionUri.toString()
                )
                db.setObject(proUser.id, ProUser::class.java, proUser)

                val intent = Intent(this, ProMainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, UPLOAD_FAILURE_MSG, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener {
                progressBar.progress = (100.0 * it.bytesTransferred / it.totalByteCount).toInt()
            }
        }
    }

}