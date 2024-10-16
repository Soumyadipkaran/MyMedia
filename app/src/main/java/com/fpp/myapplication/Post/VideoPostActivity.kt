package com.fpp.myapplication.Post

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fpp.myapplication.HomeActivity
import com.fpp.myapplication.Models.User
import com.fpp.myapplication.Models.Video
import com.fpp.myapplication.R
import com.fpp.myapplication.databinding.ActivityVideoPostBinding
import com.fpp.myapplication.utils.USER_NODE
import com.fpp.myapplication.utils.VIDEO
import com.fpp.myapplication.utils.uploadVideo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class VideoPostActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityVideoPostBinding.inflate(layoutInflater)
    }
    private var videoUrl: String? = null
    lateinit var progress: ProgressDialog

    private fun getVideoThumbnail(uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(this, uri)
            // Extract the frame at the 1-second mark (1000000 microseconds)
            retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            Log.e("VideoPostActivity", "Error generating thumbnail: ${e.message}")
            null
        } finally {
            retriever.release()
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            progress.setMessage("Uploading video...")
            progress.show()

            // Get and set the video thumbnail
            val bitmap: Bitmap? = getVideoThumbnail(it)

            // Start the upload process
            uploadVideo(uri, VIDEO, progress) { url ->
                if (url != null) {
                    binding.clickhere.text = ""
                    videoUrl = url
                    binding.selectVid.setBackgroundColor(Color.TRANSPARENT)
                    if (bitmap != null) {
                        binding.selectVid.setImageBitmap(bitmap)
                    } else {
                        Log.e("VideoPostActivity", "Failed to create thumbnail from the video URI.")
                        binding.selectVid.setImageResource(R.drawable.img_back)
                    }
                }
                progress.dismiss()  // Dismiss progress dialog after upload
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        progress = ProgressDialog(this)

        // Toolbar navigation (Back button behavior)
        binding.materialToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Cancel button
        binding.cancelBtn.setOnClickListener {
            startActivity(Intent(this@VideoPostActivity, HomeActivity::class.java))
            finish()
        }

        // Video selection
        binding.selectVid.setOnClickListener {
            launcher.launch("video/*")
        }

        // Upload button
        binding.UploadBtn.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user: User = documentSnapshot.toObject<User>()!!

                    if (videoUrl != null) {
                        // Create a new Video instance with the current timestamp
                        val video = Video(
                            videoUrl!!,
                            binding.caption.editText?.text.toString(),
                            user.image!!,
                            user.name!!,
                            com.google.firebase.Timestamp.now()  // Adding timestamp
                        )

                        // Upload to the general and user-specific collections
                        Firebase.firestore.collection(VIDEO).document().set(video)
                            .addOnSuccessListener {
                                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + VIDEO)
                                    .document()
                                    .set(video).addOnSuccessListener {
                                        startActivity(
                                            Intent(
                                                this@VideoPostActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Please select a video before uploading",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
