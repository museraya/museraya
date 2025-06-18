package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VintageRadioFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artAdapter: ArtAdapter
    private val artList = mutableListOf<ArtItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vintage_radio, container, false)

        recyclerView = view.findViewById(R.id.artRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        artAdapter = ArtAdapter(artList)
        recyclerView.adapter = artAdapter

        fetchArtFromFirestore()

        return view
    }

    private fun fetchArtFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("audio").addSnapshotListener { snapshots, error ->
            if (error != null || snapshots == null) return@addSnapshotListener

            artList.clear()

            for (doc in snapshots.documents) {
                val name = doc.getString("name") ?: continue
                val info = doc.getString("info") ?: "No info available"
                val rawUrl = doc.getString("url")
                val rawCover = doc.getString("cover")
                val rawUrl2 = doc.getString("url2")
                val rawUrl3 = doc.getString("url3")
                val rawUrl4 = doc.getString("url4")
                val rawUrl5 = doc.getString("url5")

                // Use cover for list thumbnail display
                val imageUrl = if (!rawCover.isNullOrBlank() && rawCover != "undefined") rawCover else null

                val imageResId = when (name) {
                    // Add cases if you want to set specific drawable per name
                    else -> R.drawable.placeholder
                }

                val navId = when (name) {
                    // Add navigation ID mapping if needed
                    else -> null
                }

                // ✅ Add url2 and url3 here
                artList.add(
                    ArtItem(
                        title = name,
                        imageResId = imageResId,
                        imageUrl = imageUrl,
                        info = info,
                        navId = navId,
                        url = rawUrl,
                        url2 = rawUrl2,
                        url3 = rawUrl3,
                        url4 = rawUrl4,
                        url5 = rawUrl5
                    )
                )
            }

            artAdapter.notifyDataSetChanged()
        }
    }
}
