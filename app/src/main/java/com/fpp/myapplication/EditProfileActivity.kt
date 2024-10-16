package com.fpp.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.fpp.myapplication.Fragments.ProfileFragment
import com.fpp.myapplication.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.crossEdit.setOnClickListener {
            startActivity(Intent(this@EditProfileActivity,ProfileFragment::class.java))
            finish()
        }

    }
}