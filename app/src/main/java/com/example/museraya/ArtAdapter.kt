package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

        // Load image using Glide
        if (item.imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .into(holder.image)
        } else {
            holder.image.setImageResource(item.imageResId ?: R.drawable.placeholder)
        }

        // Conditionally show/hide title
        when (item.title) {
            "Vintage Phonograph with AM Radio" -> {
                holder.title.visibility = View.GONE
            }
            else -> {
                holder.title.visibility = View.VISIBLE
                holder.title.text = item.title
            }
        }

        // Prepare bundle with all 3 image URLs
        val bundle = Bundle().apply {
            putString("name", item.title)
            putString("info", item.info)
            putString("url", item.url)
            putString("url2", item.url2)
            putString("url3", item.url3)
            putString("url4", item.url4)
            putString("url5", item.url5)
        }

        val destinationId = item.navId ?: R.id.infoFragment

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(destinationId, bundle)
        }
    }

    override fun getItemCount(): Int = artList.size
}
