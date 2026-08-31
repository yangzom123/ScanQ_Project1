package com.scanq.app.model

/**
 * Stored in Firestore at:
 *   students/{uid}   -> for role = "student"
 *   tutors/{uid}     -> for role = "tutor"
 */
data class AppUser(
    val uid: String = "",
    val fullName: String = "",
    val idNumber: String = "",   // Student ID or Tutor ID
    val email: String = "",
    val role: String = ""        // "student" or "tutor"
)
