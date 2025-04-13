package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.MaterialToolbar // Import MaterialToolbar

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // --- Toolbar Setup ---
        val toolbar: MaterialToolbar = view.findViewById(R.id.toolbar)
        // If you want the toolbar to have navigation icon or options menu, set it up
        // (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        // (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true) // Example

        // --- RecyclerView Setup ---
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Use StaggeredGridLayoutManager
        val spanCount = 2 // Number of columns
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)

        // Make sure your images actually have different aspect ratios for stagger effect!
        // If all drawables d1, d2, d3, d4 are the same size/shape, it will look like a grid.
        val catalogItems = listOf(
            CatalogItem("Vintage Audio", R.drawable.d1),
            CatalogItem("Vintage Film Camera Collection", R.drawable.d2),
            CatalogItem("Vintage Music", R.drawable.d3),
            CatalogItem("Traditional Arts", R.drawable.d4)
        )

        val adapter = CatalogAdapter(catalogItems) { clickedItem ->
            val navController = view.findNavController()
            when (clickedItem.title) {
                // Make sure titles here EXACTLY match the ones in catalogItems
                "Vintage Audio" -> navController.navigate(R.id.navigation_vintage_radio)
                "Vintage Film Camera Collection" -> navController.navigate(R.id.vintageFilmFragment)
                "Vintage Music" -> navController.navigate(R.id.vintageMusicFragment)
                "Traditional Arts" -> navController.navigate(R.id.artFragment)
            }
        }
        recyclerView.adapter = adapter

        // --- Important for NestedScrollView performance ---
        // This helps NestedScrollView fling correctly with RecyclerView
        recyclerView.isNestedScrollingEnabled = false

        return view
    }
}