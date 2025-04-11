package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VintageFilmFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var artAdapter: ArtAdapter
    private val artList = mutableListOf<ArtItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vintage_film, container, false)

        recyclerView = view.findViewById(R.id.filmRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        artAdapter = ArtAdapter(artList)
        recyclerView.adapter = artAdapter

        fetchArtFromFirestore()

        return view
    }
    private fun fetchArtFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("film").addSnapshotListener { snapshots, error ->
            if (error != null || snapshots == null) return@addSnapshotListener

            artList.clear()

            for (doc in snapshots.documents) {
                val name = doc.getString("name") ?: continue
                val info = doc.getString("info") ?: "No info available"
                val rawUrl = doc.getString("url")

                // Use image URL only if it is not blank or "undefined"
                val imageUrl = if (!rawUrl.isNullOrBlank() && rawUrl != "undefined") rawUrl else null

                val imageResId = when (name) {
                    "8 Millimeter Film Editor and Viewer" -> R.drawable.film_viewer
                    "8 Millimeter Film Camera" -> R.drawable.film_camera
                    "Polaroid Instant Photo" -> R.drawable.polaroid
                    "Sony Cassette Tape Field Recorder" -> R.drawable.cassette_recorder
                    "Portable Slide Projector (35 mm slides)" -> R.drawable.slide_projector
                    else -> R.drawable.placeholder
                }

                val navId = when (name) {
                    "8 Millimeter Film Editor and Viewer" -> R.id.filmViewerFragment
                    "8 Millimeter Film Camera" -> R.id.filmCameraFragment
                    "Polaroid Instant Photo" -> R.id.filmPolaroidFragment
                    "Sony Cassette Tape Field Recorder" -> R.id.filmCassetteFragment
                    "Portable Slide Projector (35 mm slides)" -> R.id.filmSlideFragment
                    else -> null
                }

                // Pass the URL along with other details
                artList.add(ArtItem(name, imageResId, imageUrl, info, navId))
            }

            artAdapter.notifyDataSetChanged()
        }
    }



}
