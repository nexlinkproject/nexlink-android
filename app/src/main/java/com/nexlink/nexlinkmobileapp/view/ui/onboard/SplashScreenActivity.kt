package com.nexlink.nexlinkmobileapp.view.ui.onboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.view.ui.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }, 2000)
    }
}