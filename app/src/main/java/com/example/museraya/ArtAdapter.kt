package com.example.museraya

import android.os.Bundle
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
        holder.image.setImageResource(item.imageResId)

        // Conditionally show/hide title
        when (item.title) {
            "Forester’s Nightmare” (26x36) by Art Tibaldo (2010)",
            "“Tex Reavis Panning Gold in a Benguet River” (40x30cm) by Art Tibaldo (2024)",
            "33 RPM Vinyl Records",
            "45 RPM Single Vinyl Records",
            "Vintage Phonograph with AM Radio (1940s-1950s)",
            "8 Millimeter Film Editor and Viewer",
            "8 Millimeter Film Camera",
            "Polaroid Instant Photo",
            "Sony Cassette Tape Field Recorder",
            "Portable Slide Projector (35 mm slides)",
            "Bell Telephone",
            "Nagra Open Real Field Recorder (1960s-2000s)",
            "Boom Microphones"->  {
                holder.title.visibility = View.GONE
            }
            else -> {
                holder.title.visibility = View.VISIBLE
                holder.title.text = item.title
            }
        }

        // Navigation logic
        val bundle = Bundle().apply {
            putString("name", item.title)
            putString("info", item.info)
        }

        val destinationId = when (item.title) {
            "Forester’s Nightmare” (26x36) by Art Tibaldo (2010)" -> R.id.woodcutterFragment
            "“Tex Reavis Panning Gold in a Benguet River” (40x30cm) by Art Tibaldo (2024)" -> R.id.texFragment
            "33 RPM Vinyl Records" -> R.id.vinylFragment
            "45 RPM Single Vinyl Records" -> R.id.singleVinylFragment
            "Vintage Phonograph with AM Radio (1940s-1950s)" -> R.id.turntableFragment
            "8 Millimeter Film Editor and Viewer" -> R.id.filmViewerFragment
            "8 Millimeter Film Camera" -> R.id.filmCameraFragment
            "Polaroid Instant Photo" -> R.id.filmPolaroidFragment
            "Sony Cassette Tape Field Recorder" -> R.id.filmCassetteFragment
            "Portable Slide Projector (35 mm slides)" -> R.id.filmSlideFragment
            "Bell Telephone" -> R.id.radio1
            "Nagra Open Real Field Recorder (1960s-2000s)" -> R.id.audio_narga
            "Boom Microphones" -> R.id.audioBoomFragment
            else -> R.id.infoFragment
        }

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(destinationId, bundle)
        }
    }



    override fun getItemCount(): Int = artList.size
}
