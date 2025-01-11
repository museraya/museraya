package com.example.museraya

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore

class audio_narga : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var leftArrow: ImageView
    private lateinit var rightArrow: ImageView
    private lateinit var fullScreenIcon: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_audio_narga, container, false)

        // Initialize views
        nameTextView = view.findViewById(R.id.nameTextView)
        infoTextView = view.findViewById(R.id.infoTextView)
        viewPager = view.findViewById(R.id.viewPagerImageSlider)
        leftArrow = view.findViewById(R.id.leftArrow)
        rightArrow = view.findViewById(R.id.rightArrow)
        fullScreenIcon = view.findViewById(R.id.fullScreenIcon)

        // Initialize ViewPager2 with an image adapter and sample images
        val imageList = listOf(
            R.drawable.nagra_box,
            R.drawable.nagra1,
            R.drawable.nagra2,
            R.drawable.nagra3,
            R.drawable.nagra4,
            R.drawable.nagra5,
            R.drawable.nagra6
        )
        viewPager.adapter = ImageSliderAdapter(imageList)

        // Arrow navigation
        leftArrow.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem > 0) {
                viewPager.currentItem = currentItem - 1
            }
        }

        rightArrow.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < imageList.size - 1) {
                viewPager.currentItem = currentItem + 1
            }
        }

        // Full-screen icon click listener
        fullScreenIcon.setOnClickListener {
            val currentImage = imageList[viewPager.currentItem]
            val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
            intent.putExtra("imageResId", currentImage)
            startActivity(intent)
        }

        // Fetch specific document "nagra" from Firestore
        db.collection("items").document("nagra")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("Firestore", "${document.id} => ${document.data}")
                    val name = document.getString("name") ?: "Unknown"
                    val info = document.getString("info") ?: "No info available"
                    nameTextView.text = name
                    infoTextView.text = info
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting document.", exception)
            }

        return view
    }
}
