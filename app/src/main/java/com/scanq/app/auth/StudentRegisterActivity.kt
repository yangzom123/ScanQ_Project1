package com.scanq.app.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.scanq.app.R
import com.scanq.app.model.AppUser
import com.scanq.app.student.StudentDashboardActivity

class StudentRegisterActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_register)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etStudentId = findViewById<EditText>(R.id.etStudentId)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (fullName.isEmpty() || studentId.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: return@addOnSuccessListener
                    val user = AppUser(
                        uid = uid,
                        fullName = fullName,
                        idNumber = studentId,
                        email = email,
                        role = "student"
                    )
                    db.collection("students").document(uid).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, StudentDashboardActivity::class.java))
                            finish()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
