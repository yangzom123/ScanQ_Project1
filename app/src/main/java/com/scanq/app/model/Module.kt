package com.scanq.app.model

/**
 * Firestore: modules/{moduleId}
 * A module has a tutorId (owner), a code (e.g. "ITM302"), a name,
 * and enrolled student uids in a sub-array or sub-collection.
 */
data class Module(
    val moduleId: String = "",
    val code: String = "",
    val name: String = "",
    val tutorId: String = "",
    val attendancePercent: Int = 0   // computed field, per-student when shown on student dashboard
)
