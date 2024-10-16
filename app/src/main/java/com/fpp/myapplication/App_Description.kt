package com.fpp.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fpp.myapplication.databinding.ActivityAppDescriptionBinding
import com.fpp.myapplication.databinding.ActivityLoginBinding

class App_Description : AppCompatActivity() {
    private val binding by lazy {
        ActivityAppDescriptionBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.close.setOnClickListener {
            startActivity(Intent(this@App_Description, SignUpActivity::class.java))
            finish()
        }
    }
}