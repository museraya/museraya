package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 1) // Display 1 item per row

        // Sample data for the catalog
        val catalogItems = listOf(
            CatalogItem("Vintage Audio", R.drawable.d1),
            CatalogItem("Vintage Film", R.drawable.d2),
            CatalogItem("Vintage Music", R.drawable.d3),
            CatalogItem("Paintings and Art", R.drawable.d4)
        )

        val adapter = CatalogAdapter(catalogItems) { item ->
            when (item.title) {
                "Vintage Audio" -> view.findNavController().navigate(R.id.navigation_vintage_radio)
                "Vintage Film" -> view.findNavController().navigate(R.id.vintageFilmFragment)
                "Vintage Music" -> view.findNavController().navigate(R.id.vintageMusicFragment)
                "Paintings and Art" -> view.findNavController().navigate(R.id.artFragment)
            }
        }
        recyclerView.adapter = adapter

        return view
    }
}
