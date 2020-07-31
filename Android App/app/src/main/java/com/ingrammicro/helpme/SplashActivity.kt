package com.ingrammicro.helpme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

private const val SPLASH_TIME = 3000L

class SplashActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityScope.launch {
            delay(SPLASH_TIME)
            launchMainActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!activityScope.isActive) {
            launchMainActivity()
        }
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }

    private fun launchMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}