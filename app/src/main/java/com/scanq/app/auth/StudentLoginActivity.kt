package com.scanq.app.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.scanq.app.R
import com.scanq.app.student.StudentDashboardActivity

class StudentLoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_login)

        val etStudentId = findViewById<EditText>(R.id.etStudentId)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, StudentRegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val studentId = etStudentId.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (studentId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sanitizedId = studentId.lowercase().replace(Regex("[^a-z0-9]"), "")
            val authEmail = "$sanitizedId@scan-student.app"

            auth.signInWithEmailAndPassword(authEmail, password)
                .addOnSuccessListener {
                    startActivity(Intent(this, StudentDashboardActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Login failed: check your ID and password", Toast.LENGTH_SHORT).show()
                }
        }
    }
}