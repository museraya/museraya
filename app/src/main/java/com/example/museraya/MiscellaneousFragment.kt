package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MiscellaneousFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artAdapter: ArtAdapter
    private val artList = mutableListOf<ArtItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_miscellaneous, container, false)

        recyclerView = view.findViewById(R.id.vintageMusicRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        artAdapter = ArtAdapter(artList)
        recyclerView.adapter = artAdapter

        fetchArtFromFirestore()

        return view
    }
    private fun fetchArtFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("misc").addSnapshotListener { snapshots, error ->
            if (error != null || snapshots == null) return@addSnapshotListener

            artList.clear()

            for (doc in snapshots.documents) {
                val name = doc.getString("name") ?: continue
                val info = doc.getString("info") ?: "No info available"
                val rawUrl = doc.getString("url")

                // Use image URL only if it is not blank or "undefined"
                val imageUrl = if (!rawUrl.isNullOrBlank() && rawUrl != "undefined") rawUrl else null
                val imageResId = when (name) {
                    else -> R.drawable.placeholder
                }

                val navId = when (name) {
                    else -> null
                }

                // Pass the URL along with other details
                artList.add(ArtItem(name, imageResId, imageUrl, info, navId))
            }

            artAdapter.notifyDataSetChanged()
        }
    }



}
