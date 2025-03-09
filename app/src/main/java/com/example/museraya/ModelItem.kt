package com.example.museraya.data

data class ModelItem(
    val name: String,
    val category: String,
    val fragmentId: Int,  // Store Navigation ID instead of Fragment instance
    val thumbnailResId: Int
)
