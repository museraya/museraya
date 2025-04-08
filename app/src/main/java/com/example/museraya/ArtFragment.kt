package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ArtFragment : Fragment() {

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

        db.collection("art").addSnapshotListener { snapshots, error ->
            if (error != null || snapshots == null) return@addSnapshotListener

            artList.clear()

            for (doc in snapshots.documents) {
                val name = doc.getString("name") ?: continue
                val info = doc.getString("info") ?: "No info available"

                val imageResId = when (name) {
                    "Forester’s Nightmare” (26x36) by Art Tibaldo (2010)" -> R.drawable.woodcutter
                    "“Tex Reavis Panning Gold in a Benguet River” (40x30cm) by Art Tibaldo (2024)" -> R.drawable.tex
                    else -> R.drawable.placeholder
                }

                val navId = when (name) {
                    "Forester’s Nightmare” (26x36) by Art Tibaldo (2010)" -> R.id.woodcutterFragment
                    "“Tex Reavis Panning Gold in a Benguet River” (40x30cm) by Art Tibaldo (2024)" -> R.id.texFragment
                    else -> null
                }

                artList.add(ArtItem(name, imageResId, info, navId))

            }

            artAdapter.notifyDataSetChanged()
        }
    }


}
