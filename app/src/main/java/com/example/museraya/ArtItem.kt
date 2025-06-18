package com.example.museraya

data class ArtItem(
    val title: String,
    val imageResId: Int? = null,       // fallback image resource
    val imageUrl: String? = null,      // thumbnail for the list
    val info: String = "No info available",
    val navId: Int? = null,
    val url: String? = null,           // 1st image for slider
    val url2: String? = null,          // 2nd image for slider
    val url3: String? = null,        // 3rd image for slider
    val url4: String? = null,          // 2nd image for slider
    val url5: String? = null
)
