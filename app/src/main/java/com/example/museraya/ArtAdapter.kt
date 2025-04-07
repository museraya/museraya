package com.example.museraya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class ArtAdapter(private val artList: List<ArtItem>) :
    RecyclerView.Adapter<ArtAdapter.ArtViewHolder>() {

    inner class ArtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.artImage)
        val title: TextView = itemView.findViewById(R.id.artTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_art, parent, false)
        return ArtViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        val item = artList[position]
        holder.title.text = item.title
        holder.image.setImageResource(item.imageResId)

        item.navId?.let { navId ->
            holder.itemView.setOnClickListener {
                it.findNavController().navigate(navId)
            }
        }
    }


    override fun getItemCount(): Int = artList.size
}
