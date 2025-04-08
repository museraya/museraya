package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.util.Log

import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class InfoFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        // Initialize UI components
        nameTextView = view.findViewById(R.id.textView2)
        infoTextView = view.findViewById(R.id.textView8)

        // Get the arguments passed to the fragment
        val name = arguments?.getString("name") ?: "Unknown"
        val info = arguments?.getString("info") ?: "No information available"

        // Set the received data to UI components
        nameTextView.text = name
        infoTextView.text = info



        return view
    }

}
