package com.example.museraya

data class ArtItem(
    val title: String,
    val imageResId: Int? = null, // keep this for fallback images
    val imageUrl: String? = null, // new field for URL
    val info: String = "No info available",
    val navId: Int? = null,
)
