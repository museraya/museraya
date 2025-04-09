package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class InfoFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var imageView: ImageView // Add ImageView reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        // Initialize UI components
        nameTextView = view.findViewById(R.id.textView2)
        infoTextView = view.findViewById(R.id.textView8)
        imageView = view.findViewById(R.id.imageView) // Initialize the ImageView

        // Get the arguments passed to the fragment
        val name = arguments?.getString("name") ?: "Unknown"
        val info = arguments?.getString("info") ?: "No information available"
        val imageUrl = arguments?.getString("imageUrl") // Retrieve image URL from arguments

        // Set the received data to UI components
        nameTextView.text = name
        infoTextView.text = info

        // Load the image from URL using Glide if available
        imageUrl?.let {
            Glide.with(requireContext())
                .load(it)
                .into(imageView)
        }

        return view
    }
}
