package com.github.h3lp3rs.h3lp.professional

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases
import com.github.h3lp3rs.h3lp.model.professional.CloudStorage
import com.github.h3lp3rs.h3lp.model.professional.ProUser
import com.github.h3lp3rs.h3lp.view.signin.GoogleSignInAdapter
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
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
        private val db = Databases.databaseOf(Databases.PRO_USERS)

        // Reference to the Firebase cloud storage
        private lateinit var storageRef: StorageReference

        // Type of the intent to launch when choosing an image from the device
        private const val INTENT_TYPE = "image/*"
        private const val UPLOAD_FAILURE_MSG = R.string.upload_failed
        var currentUserId = SignInActivity.userUid.toString()
        var currentUserName = GoogleSignInAdapter.auth.currentUser?.displayName.toString()
        var currentUserProofName = ""
        var currentUserProofUri = ""
        var currentUserStatus = ""
        var currentUserDomain = ""
        var currentUserExperience = ""
        private const val DELAY: Long = 5000

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
     * Opens the image file chooser of the device in order to select an image
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
            result.data?.let { data ->
                data.data?.let {
                    if (result.resultCode == Activity.RESULT_OK) {
                        imgUri = it
                        // Display the selected image
                        Picasso.with(this).load(imgUri).into(img)
                    }
                }
            }
        }

    /**
     * Retrieves the file extension
     *
     * @param uri Uri of the file
     * @return The extension of the file
     */
    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

    /**
     * Uploads the selected file into the cloud storage and stores the current user in the authenticated users
     * database
     */
    private fun uploadFile() {
        imgUri?.let { uri ->
            // Creates a file in the cloud storage with the current time as name (Sufficient to avoid collision but can be redesigned in the future )
            storageRef = CloudStorage.get()
            val fileReference: StorageReference = storageRef.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(
                    uri
                )
            )
            // Stores the file the cloud storage
            fileReference.putFile(uri).addOnSuccessListener {
                // A delay to be able to see the progress bar at 100% before redirection
                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.progress = 0
                }, DELAY)

                currentUserProofName = fileName.text.toString().trim()
                currentUserProofUri = it.uploadSessionUri.toString()

                val proUser = ProUser(
                    id = currentUserId,
                    name = currentUserName,
                    proofName = currentUserProofName,
                    proofUri = currentUserProofUri,
                    proStatus = currentUserStatus,
                    proDomain = currentUserDomain,
                    proExperience = currentUserExperience
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