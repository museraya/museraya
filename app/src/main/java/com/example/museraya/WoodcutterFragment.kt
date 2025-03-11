package com.example.museraya

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class WoodcutterFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var arButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_woodcutter, container, false)

        // Initialize UI components
        nameTextView = view.findViewById(R.id.textView2)
        infoTextView = view.findViewById(R.id.textView8)
        arButton = view.findViewById(R.id.button)

        // Fetch data from Firestore
        db.collection("art").document("Wh3B66PNeBQKeP7erVGw")
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

        // Handle button click
        arButton.setOnClickListener {
            // Navigate to another fragment or activity
            findNavController().navigate(R.id.action_woodcutterFragment_to_navigation_liveview)
        }

        return view
    }
}
