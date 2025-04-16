package com.example.museraya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController // Can keep this or use the extension
import androidx.navigation.fragment.findNavController // Cleaner extension
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
        // (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        // --- RecyclerView Setup ---
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val spanCount = 2
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)

        // --- Reorder the catalogItems list ---
        val catalogItems = listOf(
            CatalogItem("Vintage Audio", R.drawable.d1),
            CatalogItem("Vintage Film Camera Collection", R.drawable.d2),
            // Miscellaneous moved lower
            CatalogItem("Vintage Music", R.drawable.d3),
            CatalogItem("Traditional Arts", R.drawable.d4),
            CatalogItem("Miscellaneous", R.drawable.misc)
        )

        val adapter = CatalogAdapter(catalogItems) { clickedItem ->
            val navController = findNavController() // Use fragment extension
            when (clickedItem.title) {
                "Vintage Audio" -> navController.navigate(R.id.navigation_vintage_radio)
                "Vintage Film Camera Collection" -> navController.navigate(R.id.vintageFilmFragment)
                "Vintage Music" -> navController.navigate(R.id.vintageMusicFragment)
                "Traditional Arts" -> navController.navigate(R.id.artFragment)
                "Miscellaneous" -> navController.navigate(R.id.navigation_miscellaneous) // Navigation case remains the same
            }
        }
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false

        return view
    }
}