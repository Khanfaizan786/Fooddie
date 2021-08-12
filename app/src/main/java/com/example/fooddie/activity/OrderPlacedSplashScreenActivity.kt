package com.example.fooddie.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.example.fooddie.R

class OrderPlacedSplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_order_placed_splash_screen)

        val handler = Handler()
        handler.postDelayed({
            startActivity(Intent(this@OrderPlacedSplashScreenActivity, HomeActivity::class.java))
            finish()
        }, 2000)
    }
}
