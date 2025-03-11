package com.example.museraya

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class FilmCassetteFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var infoTextView: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_film_cassette, container, false)

        nameTextView = view.findViewById(R.id.textView2)
        infoTextView = view.findViewById(R.id.textView8)

        db.collection("film").document("3RbLjfxHLg5q3qSs2AG8")
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