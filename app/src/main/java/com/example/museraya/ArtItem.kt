package com.example.museraya

data class ArtItem(
    val title: String,
    val imageResId: Int,
    val navId: Int? = null // ← this is the correct field name
)
