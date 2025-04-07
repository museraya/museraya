package com.example.museraya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class VintageAudioAdapter(
    private val items: List<VintageAudioItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<VintageAudioAdapter.AudioViewHolder>() {

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.audioImage)
        val title = itemView.findViewById<TextView>(R.id.audioTitle)

        init {
            itemView.setOnClickListener {
                onItemClick(items[adapterPosition].destinationId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vintage_audio, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.imageResId)
        holder.title.text = item.title
    }

    override fun getItemCount(): Int = items.size
}
