package com.scanq.app.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scanq.app.R
import com.scanq.app.model.Module

class ModuleAdapter(private val modules: List<Module>) :
    RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>() {

    class ModuleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val code: TextView = view.findViewById(R.id.tvModuleCode)
        val name: TextView = view.findViewById(R.id.tvModuleName)
        val percent: TextView = view.findViewById(R.id.tvPercent)
        val status: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module_student, parent, false)
        return ModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]
        holder.code.text = module.code
        holder.name.text = module.name
        holder.percent.text = "${module.attendancePercent}%"
        holder.status.text = when {
            module.attendancePercent >= 95 -> "Excellent"
            module.attendancePercent >= 90 -> "Good"
            else -> "Below 90%"
        }
    }

    override fun getItemCount() = modules.size
}
