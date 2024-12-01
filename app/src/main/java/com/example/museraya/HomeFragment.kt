package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // Display 2 items per row

        // Sample data for the catalog
        val catalogItems = listOf(
            CatalogItem("Vintage Audio", R.drawable.d1),
            CatalogItem("Vintage Film", R.drawable.d2),
            CatalogItem("Vintage Music", R.drawable.d3),
            CatalogItem("Paintings and Art", R.drawable.d4)
        )


        val adapter = CatalogAdapter(catalogItems) { item ->
            if (item.title == "Vintage Audio") {
                view.findNavController().navigate(R.id.navigation_vintage_radio)
            }
            if (item.title == "Vintage Film") {
                view.findNavController().navigate(R.id.vintageFilmFragment)
            }
            if (item.title == "Vintage Music") {
                view.findNavController().navigate(R.id.vintageMusicFragment)
            }
        }
        recyclerView.adapter = adapter

        return view
    }
}
