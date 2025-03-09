package com.example.museraya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.museraya.R
import com.example.museraya.data.ModelItem

class ModelAdapter(
    private val models: List<ModelItem>,
    private val onModelSelected: (ModelItem) -> Unit
) : RecyclerView.Adapter<ModelAdapter.ModelViewHolder>() {

    inner class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.modelName)
        val category: TextView = itemView.findViewById(R.id.modelCategory)
        val thumbnail: ImageView = itemView.findViewById(R.id.modelThumbnail)
        val selectButton: Button = itemView.findViewById(R.id.selectModelButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_model, parent, false)
        return ModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = models[position]
        holder.name.text = model.name
        holder.category.text = model.category
        holder.thumbnail.setImageResource(model.thumbnailResId)
        holder.selectButton.setOnClickListener { onModelSelected(model) }
    }

    override fun getItemCount() = models.size
}
