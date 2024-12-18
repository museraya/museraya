package com.example.museraya

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore

class audio_narga : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var viewPager: ViewPager2
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_audio_narga, container, false)

        // Initialize views
        nameTextView = view.findViewById(R.id.nameTextView)
        infoTextView = view.findViewById(R.id.infoTextView)
        viewPager = view.findViewById(R.id.viewPagerImageSlider)

        // Initialize ViewPager2 with an image adapter and sample images
        val imageList = listOf(R.drawable.nagra_box, R.drawable.nagra1, R.drawable.nagra2, R.drawable.nagra3, R.drawable.nagra4, R.drawable.nagra5, R.drawable.nagra6)
        viewPager.adapter = ImageSliderAdapter(imageList)

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
