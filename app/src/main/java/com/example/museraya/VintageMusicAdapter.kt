package com.example.museraya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.museraya.databinding.ItemVintageMusicBinding

class VintageMusicAdapter(
    private val items: List<VintageMusicItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<VintageMusicAdapter.VintageMusicViewHolder>() {

    inner class VintageMusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.musicImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VintageMusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vintage_music, parent, false)
        return VintageMusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: VintageMusicViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.imageResId)

        holder.itemView.setOnClickListener {
            onItemClick(item.destinationId)
        }
    }

    override fun getItemCount(): Int = items.size
}
