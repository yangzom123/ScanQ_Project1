package com.scanq.app.model

/**
 * Firestore: sessions/{sessionId}
 * Created when a tutor taps "Generate QR" - encodes sessionId + moduleId
 * into the QR code. Students scan it to write an attendance record.
 */
data class Session(
    val sessionId: String = "",
    val moduleId: String = "",
    val moduleName: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val totalStudents: Int = 0,
    val presentCount: Int = 0
)

/** Firestore: sessions/{sessionId}/attendance/{studentUid} */
data class AttendanceRecord(
    val studentId: String = "",
    val timestamp: Long = 0L,
    val status: String = "present"
)
