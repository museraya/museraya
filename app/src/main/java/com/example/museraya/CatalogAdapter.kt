package com.example.museraya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CatalogAdapter(private val items: List<CatalogItem>) :
    RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder>() {

    class CatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val itemTitleTextView: TextView = itemView.findViewById(R.id.itemTitleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalog, parent, false)
        return CatalogViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val item = items[position]
        holder.itemImageView.setImageResource(item.imageResId)
        holder.itemTitleTextView.text = item.title
    }

    override fun getItemCount(): Int = items.size
}
