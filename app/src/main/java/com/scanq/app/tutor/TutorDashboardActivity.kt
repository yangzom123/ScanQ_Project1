package com.scanq.app.tutor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.scanq.app.R
import com.scanq.app.model.Module
import com.scanq.app.model.Session

class TutorDashboardActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_dashboard)

        val tvTutorName = findViewById<TextView>(R.id.tvTutorName)
        val tvModuleCount = findViewById<TextView>(R.id.tvModuleCount)
        val tvStudentCount = findViewById<TextView>(R.id.tvStudentCount)
        val tvSessionCount = findViewById<TextView>(R.id.tvSessionCount)
        val tvAttendanceRate = findViewById<TextView>(R.id.tvAttendanceRate)
        val etModuleName = findViewById<EditText>(R.id.etModuleName)
        val btnCreateModule = findViewById<Button>(R.id.btnCreateModule)
        val rvSessions = findViewById<RecyclerView>(R.id.rvSessions)
        rvSessions.layoutManager = LinearLayoutManager(this)

        val uid = auth.currentUser?.uid ?: return

        db.collection("tutors").document(uid).get()
            .addOnSuccessListener { doc ->
                tvTutorName.text = doc.getString("fullName") ?: "Tutor"
            }

        loadModulesAndStats(uid, tvModuleCount, tvStudentCount, tvAttendanceRate)
        loadRecentSessions(uid, rvSessions, tvSessionCount)

        btnCreateModule.setOnClickListener {
            val name = etModuleName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter a module name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newModule = hashMapOf(
                "name" to name,
                "code" to name.take(6).uppercase(),
                "tutorId" to uid,
                "enrolledStudentIds" to listOf<String>()
            )
            db.collection("modules").add(newModule)
                .addOnSuccessListener {
                    Toast.makeText(this, "Module created", Toast.LENGTH_SHORT).show()
                    etModuleName.text.clear()
                    loadModulesAndStats(uid, tvModuleCount, tvStudentCount, tvAttendanceRate)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                // TODO: wire nav_modules, nav_students, nav_profile to their activities
                else -> true
            }
        }

        // TODO: findViewById<ImageButton>(R.id.fabGenerateQr) / cardGenerateQr ->
        // create a new doc in "sessions", encode {sessionId, moduleId} as JSON,
        // render it with ZXing's BarcodeEncoder, and display it full-screen for students to scan.
    }

    private fun loadModulesAndStats(
        uid: String,
        tvModuleCount: TextView,
        tvStudentCount: TextView,
        tvAttendanceRate: TextView
    ) {
        db.collection("modules").whereEqualTo("tutorId", uid).get()
            .addOnSuccessListener { result ->
                val modules = result.documents.map { doc ->
                    Module(
                        moduleId = doc.id,
                        code = doc.getString("code") ?: "",
                        name = doc.getString("name") ?: "",
                        tutorId = uid
                    )
                }
                tvModuleCount.text = modules.size.toString()

                @Suppress("UNCHECKED_CAST")
                val allStudentIds = result.documents
                    .flatMap { (it.get("enrolledStudentIds") as? List<String>) ?: emptyList() }
                    .distinct()
                tvStudentCount.text = allStudentIds.size.toString()

                // Overall attendance rate would normally be computed by aggregating
                // sessions/{id}/attendance subcollections across this tutor's modules.
                tvAttendanceRate.text = "--"
            }
    }

    private fun loadRecentSessions(uid: String, rvSessions: RecyclerView, tvSessionCount: TextView) {
        db.collection("sessions")
            .whereEqualTo("tutorId", uid)
            .orderBy("date")
            .limitToLast(5)
            .get()
            .addOnSuccessListener { result ->
                val sessions = result.documents.map { doc ->
                    Session(
                        sessionId = doc.id,
                        moduleName = doc.getString("moduleName") ?: "",
                        date = doc.getString("date") ?: "",
                        startTime = doc.getString("startTime") ?: "",
                        endTime = doc.getString("endTime") ?: "",
                        totalStudents = (doc.getLong("totalStudents") ?: 0).toInt(),
                        presentCount = (doc.getLong("presentCount") ?: 0).toInt()
                    )
                }
                rvSessions.adapter = SessionAdapter(sessions.reversed())
                tvSessionCount.text = result.size().toString()
            }
    }
}
