package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VintageMusicFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artAdapter: ArtAdapter
    private val artList = mutableListOf<ArtItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_art, container, false)

        recyclerView = view.findViewById(R.id.artRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        artAdapter = ArtAdapter(artList)
        recyclerView.adapter = artAdapter

        fetchArtFromFirestore()

        return view
    }
    private fun fetchArtFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("music").addSnapshotListener { snapshots, error ->
            if (error != null || snapshots == null) return@addSnapshotListener

            artList.clear()

            for (doc in snapshots.documents) {
                val name = doc.getString("name") ?: continue
                val info = doc.getString("info") ?: "No info available"
                val imageUrl = doc.getString("url") // Fetch the URL from Firebase

                val imageResId = when (name) {
                    "33 RPM Vinyl Records" -> R.drawable.vinyl
                    "45 RPM Single Vinyl Records" -> R.drawable.single_vinyl
                    "Vintage Phonograph with AM Radio (1940s-1950s)" -> R.drawable.turntable
                    else -> R.drawable.placeholder
                }

                val navId = when (name) {
                    "33 RPM Vinyl Records" -> R.id.vinylFragment
                    "45 RPM Single Vinyl Records" -> R.id.singleVinylFragment
                    "Vintage Phonograph with AM Radio (1940s-1950s)" -> R.id.turntableFragment
                    else -> null
                }

                // Pass the URL along with other details
                artList.add(ArtItem(name, imageResId, imageUrl, info, navId))
            }

            artAdapter.notifyDataSetChanged()
        }
    }



}
