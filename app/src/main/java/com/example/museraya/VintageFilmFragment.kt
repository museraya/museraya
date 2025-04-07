package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VintageFilmFragment : Fragment() {

    private lateinit var filmRecyclerView: RecyclerView
    private lateinit var filmList: ArrayList<VintageItem>
    private lateinit var adapter: VintageFilmAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vintage_film, container, false)

        filmRecyclerView = view.findViewById(R.id.filmRecyclerView)
        filmRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        filmRecyclerView.setHasFixedSize(true)

        filmList = arrayListOf(
            VintageItem(R.drawable.film_viewer, R.id.filmViewerFragment),
            VintageItem(R.drawable.film_camera, R.id.filmCameraFragment),
            VintageItem(R.drawable.polaroid, R.id.filmPolaroidFragment),
            VintageItem(R.drawable.cassette_recorder, R.id.filmCassetteFragment),
            VintageItem(R.drawable.slide_projector, R.id.filmSlideFragment)
        )

        adapter = VintageFilmAdapter(filmList) { destinationId ->
            view.findNavController().navigate(destinationId)
        }

        filmRecyclerView.adapter = adapter

        return view
    }
}
