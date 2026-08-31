package com.scanq.app.student

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.scanq.app.R
import com.scanq.app.model.Module

class StudentDashboardActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        val tvStudentName = findViewById<TextView>(R.id.tvStudentName)
        val tvOverallPercent = findViewById<TextView>(R.id.tvOverallPercent)
        val progressRing = findViewById<ProgressBar>(R.id.progressRing)
        val rvModules = findViewById<RecyclerView>(R.id.rvModules)
        rvModules.layoutManager = LinearLayoutManager(this)

        val uid = auth.currentUser?.uid ?: return

        // Load the logged-in student's profile
        db.collection("students").document(uid).get()
            .addOnSuccessListener { doc ->
                tvStudentName.text = doc.getString("fullName") ?: "Student"
            }

        // Load this student's enrolled modules
        // Firestore schema: modules/{moduleId} has an "enrolledStudentIds" array field
        db.collection("modules")
            .whereArrayContains("enrolledStudentIds", uid)
            .get()
            .addOnSuccessListener { result ->
                val modules = result.documents.map { doc ->
                    Module(
                        moduleId = doc.id,
                        code = doc.getString("code") ?: "",
                        name = doc.getString("name") ?: "",
                        tutorId = doc.getString("tutorId") ?: "",
                        // In a full build, compute this from the attendance subcollection
                        // for this student rather than reading a static field.
                        attendancePercent = (doc.get("attendance_$uid") as? Long)?.toInt() ?: 0
                    )
                }
                rvModules.adapter = ModuleAdapter(modules)

                val overall = if (modules.isNotEmpty()) modules.map { it.attendancePercent }.average().toInt() else 0
                tvOverallPercent.text = "$overall%"
                progressRing.progress = overall
            }

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                // TODO: wire nav_modules, nav_attendance, nav_profile to their activities
                else -> true
            }
        }

        // TODO: findViewById<ImageButton>(R.id.fabScan) / cardScanQr -> launch ZXing scanner,
        // read {sessionId, moduleId} from the QR, then write to
        // sessions/{sessionId}/attendance/{uid} in Firestore.
    }
}
