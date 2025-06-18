package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide

class InfoFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        // Initialize UI components
        nameTextView = view.findViewById(R.id.textView2)
        infoTextView = view.findViewById(R.id.textView8)
        viewPager = view.findViewById(R.id.viewPager)

        // Get the arguments passed to the fragment
        val name = arguments?.getString("name") ?: "Unknown"
        val info = arguments?.getString("info") ?: "No information available"

        // Collect up to 3 image URLs
        val imageList = listOfNotNull(
            arguments?.getString("url"),
            arguments?.getString("url2"),
            arguments?.getString("url3"),
            arguments?.getString("url4"),
            arguments?.getString("url5")
        ).ifEmpty {
            listOf("placeholder") // Use keyword for placeholder
        }

        // Set data to UI components
        nameTextView.text = name
        infoTextView.text = info

        // Set up the image slider adapter
        viewPager.adapter = ImageSliderAdapter2(requireContext(), imageList)

        return view
    }
}
