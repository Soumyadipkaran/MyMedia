package com.fpp.myapplication.Post

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fpp.myapplication.HomeActivity
import com.fpp.myapplication.Models.Post
import com.fpp.myapplication.Models.User
import com.fpp.myapplication.databinding.ActivityPicturePostBinding
import com.fpp.myapplication.utils.POST
import com.fpp.myapplication.utils.USER_NODE
import com.fpp.myapplication.utils.uploadImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class PicturePostActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityPicturePostBinding.inflate(layoutInflater)
    }
    var imgUrl: String? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, POST) { url ->
                if (url != null) {
                    binding.img.setImageURI(uri)
                    binding.clickhere.text = ""
                    imgUrl = url
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set toolbar navigation action
        binding.materialToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Cancel action
        binding.cancelBtn.setOnClickListener {
            startActivity(Intent(this@PicturePostActivity, HomeActivity::class.java))
        }

        // Image picker
        binding.img.setOnClickListener {
            launcher.launch("image/*")
        }

        // Upload action
        binding.UploadBtn.setOnClickListener {
            if (imgUrl != null) {
                // Retrieve current user details
                Firebase.firestore.collection(USER_NODE)
                    .document(Firebase.auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val user: User? = documentSnapshot.toObject<User>()

                        // Ensure the user object is not null
                        if (user != null) {
                            // Create a post object with user details and timestamp
                            val post = Post(
                                postUrl = imgUrl!!,
                                caption = binding.caption.editText?.text.toString(),
                                profile_img = user.image,  // User's profile image
                                uid = Firebase.auth.currentUser!!.uid,     // User's name
                                timestamp = com.google.firebase.Timestamp.now(), // Current timestamp
                                time = System.currentTimeMillis().toString()
                            )

                            // Upload post to both global Post collection and user-specific collection
                            Firebase.firestore.collection(POST).document().set(post)
                                .addOnSuccessListener {
                                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + POST)
                                        .document().set(post)
                                        .addOnSuccessListener {
                                            startActivity(
                                                Intent(this@PicturePostActivity, HomeActivity::class.java)
                                            )
                                            finish()
                                        }
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Error fetching user details.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please select an image before uploading", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
