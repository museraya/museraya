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
        val artImage: ImageView = itemView.findViewById(R.id.artImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_art, parent, false)
        return ArtViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        val artItem = artList[position]
        holder.artImage.setImageResource(artItem.imageResId)

        holder.itemView.setOnClickListener {
            it.findNavController().navigate(artItem.fragmentId)
        }
    }

    override fun getItemCount(): Int = artList.size
}
