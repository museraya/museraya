package com.example.museraya

data class ArtItem(
    val title: String,
    val imageResId: Int,
    val info: String = "No info available",
    val navId: Int? = null
)

