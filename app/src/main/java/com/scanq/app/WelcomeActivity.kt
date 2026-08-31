package com.scanq.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.scanq.app.auth.StudentLoginActivity
import com.scanq.app.auth.TutorLoginActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        findViewById<Button>(R.id.btnContinueStudent).setOnClickListener {
            startActivity(Intent(this, StudentLoginActivity::class.java))
        }

        findViewById<Button>(R.id.btnContinueTutor).setOnClickListener {
            startActivity(Intent(this, TutorLoginActivity::class.java))
        }
    }
}
