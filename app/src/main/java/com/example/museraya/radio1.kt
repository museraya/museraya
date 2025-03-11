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

class radio1 : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_radio1, container, false)

        // variables ng mga icons and resources
        nameTextView = view.findViewById(R.id.textView2)
        infoTextView = view.findViewById(R.id.textView8)
        viewPager = view.findViewById(R.id.viewPagerImageSlider)
        leftArrow = view.findViewById(R.id.leftArrow)
        rightArrow = view.findViewById(R.id.rightArrow)
        fullScreenIcon = view.findViewById(R.id.fullScreenIcon)

        // dito lahat ng images na islaslide
        val imageList = listOf(
            R.drawable.bell_telephone_box,
            R.drawable.btb2,
            R.drawable.btb3,
            R.drawable.btb4
        )
        viewPager.adapter = ImageSliderAdapter(imageList)

        // Arrow navigation
        leftArrow.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem > 0) {
                viewPager.currentItem = currentItem - 1
            }
        }

        // next image to the right
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

        // Retrieve item details from Firestore
        db.collection("audio").document("EuOjXwyYQgA7kM0HSsWI")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "Unknown"
                    val info = document.getString("info") ?: "No info available"
                    nameTextView.text = name
                    infoTextView.text = info
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting document.", exception)
            }

        return view
    }
}
