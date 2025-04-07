package com.example.museraya

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class VintageFilmAdapter(
    private val filmList: List<VintageItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<VintageFilmAdapter.FilmViewHolder>() {

    class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filmImage: ImageView = itemView.findViewById(R.id.filmItemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vintage_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val item = filmList[position]
        holder.filmImage.setImageResource(item.imageRes)
        holder.itemView.setOnClickListener {
            onItemClick(item.destinationId)
        }
    }

    override fun getItemCount(): Int = filmList.size
}
