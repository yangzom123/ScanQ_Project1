package com.scanq.app.tutor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scanq.app.R
import com.scanq.app.model.Session

class SessionAdapter(private val sessions: List<Session>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moduleName: TextView = view.findViewById(R.id.tvSessionModule)
        val time: TextView = view.findViewById(R.id.tvSessionTime)
        val percent: TextView = view.findViewById(R.id.tvSessionPercent)
        val present: TextView = view.findViewById(R.id.tvSessionPresent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session_tutor, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        holder.moduleName.text = session.moduleName
        holder.time.text = "${session.date} : ${session.startTime}-${session.endTime}"
        val pct = if (session.totalStudents > 0) (session.presentCount * 100 / session.totalStudents) else 0
        holder.percent.text = "$pct%"
        holder.present.text = "Present: ${session.presentCount}/${session.totalStudents}"
    }

    override fun getItemCount() = sessions.size
}
