package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArtFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_art, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.artRecyclerView)

        val artList = listOf(
            ArtItem("Woodcutter", R.drawable.woodcutter, R.id.woodcutterFragment),
            ArtItem("Tex", R.drawable.tex, R.id.texFragment)
            // Add more if you have more art
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ArtAdapter(artList)

        return view
    }
}
