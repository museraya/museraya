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

                // Use cover for list thumbnail display
                val imageUrl = if (!rawCover.isNullOrBlank() && rawCover != "undefined") rawCover else null

                val imageResId = when (name) {
//                    "Bell Telephone" -> R.drawable.bell_telephone
//                    "Nagra Open Real Field Recorder (1960s-2000s)" -> R.drawable.nagra
//                    "Boom Microphones" -> R.drawable.boom_mic
                    else -> R.drawable.placeholder
                }

                val navId = when (name) {
//                    "Bell Telephone" -> R.id.radio1
//                    "Nagra Open Real Field Recorder (1960s-2000s)" -> R.id.audio_narga
//                    "Boom Microphones" -> R.id.audioBoomFragment
                    else -> null
                }

                // Send actual "url" to InfoFragment, but show "cover" in the list
                artList.add(ArtItem(name, imageResId, imageUrl, info, navId, url = rawUrl))
            }

            artAdapter.notifyDataSetChanged()
        }
    }
}
