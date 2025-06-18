package com.example.museraya

data class ArtItem(
    val title: String,
    val imageResId: Int? = null,       // fallback image resource
    val imageUrl: String? = null,      // image to be used in the RecyclerView (cover)
    val info: String = "No info available",
    val navId: Int? = null,
    val url: String? = null            // original full-sized image for InfoFragment
)
