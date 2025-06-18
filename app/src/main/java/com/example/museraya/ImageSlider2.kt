package com.example.museraya

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageSliderAdapter2(
    private val context: Context,
    private val imageUrls: List<String>
) : RecyclerView.Adapter<ImageSliderAdapter2.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.sliderImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slider_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        if (imageUrl == "placeholder") {
            holder.imageView.setImageResource(R.drawable.placeholder)
        } else {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}
