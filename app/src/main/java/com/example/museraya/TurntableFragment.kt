package com.example.museraya

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class TurntableFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var samplesRecyclerView: RecyclerView
    private lateinit var vinylImageView: ImageView
    private val db = FirebaseFirestore.getInstance()
    private val sampleTracks = listOf(
        "A Big Hunk o' Love",
        "Love Me",
        "Stuck on You",
        "Good Luck Charm",
        "Return to Sender (from the Paramount Picture 'Girls! Girls! Girls!')"
    )

    private var isVinylSpinning = false
    private var rotateAnimation: RotateAnimation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_turntable, container, false)

        // Initialize views
        nameTextView = view.findViewById(R.id.textView2)
        infoTextView = view.findViewById(R.id.textView8)
        samplesRecyclerView = view.findViewById(R.id.samplesRecyclerView)
        vinylImageView = view.findViewById(R.id.vinylDisk)

        // Load Firestore data
        db.collection("music").document("hVvc6QqxV7uIAempGcJb")
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

        // Set up RecyclerView for SAMPLES section
        val adapter = SampleTrackAdapter(requireContext(), sampleTracks).apply {
            setPlaybackListener(object : PlaybackListener {
                override fun onTrackPlay() {
                    startVinylAnimation()
                }

                override fun onTrackPause() {
                    stopVinylAnimation()
                }
            })
        }
        samplesRecyclerView.layoutManager = LinearLayoutManager(context)
        samplesRecyclerView.adapter = adapter

        return view
    }

    private fun startVinylAnimation() {
        if (isVinylSpinning) return
        isVinylSpinning = true

        rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 3000
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
        }

        vinylImageView.startAnimation(rotateAnimation)
    }

    private fun stopVinylAnimation() {
        isVinylSpinning = false
        vinylImageView.clearAnimation()
    }

    interface PlaybackListener {
        fun onTrackPlay()
        fun onTrackPause()
    }
}
