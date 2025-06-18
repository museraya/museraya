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

        // Use Glide to load the image from URL
        if (item.imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .into(holder.image) // Load from URL
        } else {
            // If URL is not available, use the fallback resource
            holder.image.setImageResource(item.imageResId ?: R.drawable.placeholder)
        }

        // Conditionally show/hide title (your existing logic)
        when (item.title) {
//            "Forester’s Nightmare” (26x36) by Art Tibaldo (2010)",
//            "“Tex Reavis Panning Gold in a Benguet River” (40x30cm) by Art Tibaldo (2024)",
//            "33 RPM Vinyl Records",
//            "45 RPM Single Vinyl Records",
//            "8 Millimeter Film Editor and Viewer",
//            "8 Millimeter Film Camera",
//            "Polaroid Instant Photo",
//            "Sony Cassette Tape Field Recorder",
//            "Portable Slide Projector (35 mm slides)",
//            "Bell Telephone",
//            "Nagra Open Real Field Recorder (1960s-2000s)",
//            "Boom Microphones",
            "Vintage Phonograph with AM Radio",-> {
                holder.title.visibility = View.GONE
            }
            else -> {
                holder.title.visibility = View.VISIBLE
                holder.title.text = item.title
            }
        }

        // Navigation logic (your existing logic)
        val bundle = Bundle().apply {
            putString("name", item.title)
            putString("info", item.info)
            putString("imageUrl", item.url) // pass full-sized image for detail view
        }


        val destinationId = item.navId ?: R.id.infoFragment

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(destinationId, bundle)
        }
    }



    override fun getItemCount(): Int = artList.size
}
