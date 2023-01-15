package com.example.chatgpt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.chatgpt.databinding.ActivityLaunchBinding

class LaunchActivity : AppCompatActivity() {
    lateinit var binding: ActivityLaunchBinding
    private val SPLASH_TIME_OUT:Long = 4000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.laSplashAnimation.visibility = View.VISIBLE
        binding.laSplashAnimation.playAnimation()
        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }
}