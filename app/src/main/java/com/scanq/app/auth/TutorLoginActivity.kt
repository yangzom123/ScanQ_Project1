package com.scanq.app.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.scanq.app.R
import com.scanq.app.tutor.TutorDashboardActivity

class TutorLoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_login)

        val etTutorId = findViewById<EditText>(R.id.etTutorId)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, TutorRegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val tutorId = etTutorId.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (tutorId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tutors sign in with Tutor ID, but Firebase Auth needs an email.
            // We look up the linked email in Firestore first, then sign in.
            db.collection("tutors")
                .whereEqualTo("idNumber", tutorId)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        Toast.makeText(this, "Tutor ID not found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    val email = result.documents[0].getString("email") ?: ""
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            startActivity(Intent(this, TutorDashboardActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
