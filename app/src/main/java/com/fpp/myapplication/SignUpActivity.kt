package com.fpp.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fpp.myapplication.Models.User
import com.fpp.myapplication.databinding.ActivitySignUpBinding
import com.fpp.myapplication.utils.USER_NODE
import com.fpp.myapplication.utils.USER_PROFILE_FOLDER
import com.fpp.myapplication.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {

    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if (it == null) {
                    // Handle error case
                } else {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize empty user object
        user = User()

        // Check if we are in update profile mode (MODE == 1)
        if (intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
            binding.signUpBtn.text = "Update Profile"
            binding.textView.text = ""
            binding.email.visibility = View.GONE
            binding.password.visibility= View.GONE
            binding.toLogin.text = ""

            // Fetch existing user data from Firestore
            Firebase.firestore.collection(USER_NODE)
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    user = it.toObject<User>() ?: User()
                    // Load user data into the UI fields
                    binding.name.editText?.setText(user.name ?: "")
                    binding.email.editText?.setText(user.email ?: "")
                    binding.password.editText?.setText(user.password ?: "")

                    // Load profile image
                    if (!user.image.isNullOrEmpty()) {
                        Picasso.get().load(user.image).into(binding.profileImage)
                    }
                }
        }

        // Handle sign-up button click
        binding.signUpBtn.setOnClickListener {
            if (intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
                // Update existing profile
                updateUserProfile()
            } else {
                // Handle new user sign-up
                handleSignUp()
            }
        }

        // Add profile image click listener
        binding.AddImg.setOnClickListener {
            launcher.launch("image/*")
        }

        // Redirect to login
        binding.toLogin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }
        binding.about.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, App_Description::class.java))
            finish()
        }
    }

    private fun updateUserProfile() {
        // Update user object with new data
        user.name = binding.name.editText?.text.toString()
        user.email = binding.email.editText?.text.toString()

        // Update Firestore user document
        Firebase.firestore.collection(USER_NODE)
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleSignUp() {
        // Validate user input
        if (binding.name.editText?.text.toString().isEmpty() ||
            binding.email.editText?.text.toString().isEmpty() ||
            binding.password.editText?.text.toString().isEmpty()) {

            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create new user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            binding.email.editText?.text.toString(),
            binding.password.editText?.text.toString()
        ).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                // Set user data
                user.name = binding.name.editText?.text.toString()
                user.email = binding.email.editText?.text.toString()
                user.password = binding.password.editText?.text.toString()
                user.uid = FirebaseAuth.getInstance().currentUser!!.uid

                // Save new user to Firestore
                Firebase.firestore.collection(USER_NODE)
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save user", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
