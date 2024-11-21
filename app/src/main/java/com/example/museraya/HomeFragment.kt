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
            CatalogItem("Vintage Radio", R.drawable.d1),
            CatalogItem("Vintage Camera", R.drawable.d2),
            CatalogItem("Vintage Radio 2", R.drawable.d3),
            CatalogItem("Vintage Camera 2", R.drawable.d4)
        )


        val adapter = CatalogAdapter(catalogItems) { item ->
            if (item.title == "Vintage Radio") {
                view.findNavController().navigate(R.id.navigation_vintage_radio)
            }
        }
        recyclerView.adapter = adapter

        return view
    }
}
