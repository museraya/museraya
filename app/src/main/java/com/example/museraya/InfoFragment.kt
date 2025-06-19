package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

class InfoFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: LinearLayout
    private lateinit var dots: Array<ImageView?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        // Initialize UI components
        nameTextView = view.findViewById(R.id.textView2)
        infoTextView = view.findViewById(R.id.textView8)
        viewPager = view.findViewById(R.id.viewPager)
        dotsIndicator = view.findViewById(R.id.dotsIndicator)

        // Get the arguments passed to the fragment
        val name = arguments?.getString("name") ?: "Unknown"
        val info = arguments?.getString("info") ?: "No information available"

        // Filter and collect valid image URLs
        val rawUrls = listOf(
            arguments?.getString("url"),
            arguments?.getString("url2"),
            arguments?.getString("url3"),
            arguments?.getString("url4"),
            arguments?.getString("url5")
        )

        val validUrls = rawUrls.filterNotNull()
            .filter { it.isNotBlank() && it != "https://via.placeholder.com/100?text=No+Image" }

        val imageList = if (validUrls.isEmpty()) {
            listOf("placeholder") // Show a single placeholder image only when nothing valid
        } else {
            validUrls
        }

        // Set data to UI components
        nameTextView.text = name
        infoTextView.text = info

        // Set up the image slider adapter
        viewPager.adapter = ImageSliderAdapter2(requireContext(), imageList)

        // Setup dots indicator
        setupDotsIndicator(imageList.size)
        setCurrentDot(0)

        // Add page change listener for dots
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentDot(position)
            }
        })

        return view
    }

    private fun setupDotsIndicator(count: Int) {
        dots = arrayOfNulls(count)
        dotsIndicator.removeAllViews()

        for (i in 0 until count) {
            dots[i] = ImageView(requireContext())
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.dot_inactive
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dotsIndicator.addView(dots[i], params)
        }
    }

    private fun setCurrentDot(position: Int) {
        for (i in dots.indices) {
            val drawableId = if (i == position) {
                R.drawable.dot_active
            } else {
                R.drawable.dot_inactive
            }
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), drawableId)
            )
        }
    }
}