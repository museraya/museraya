package com.example.museraya

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FullScreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageView: ImageView = findViewById(R.id.fullScreenImageView)
        val imageResId = intent.getIntExtra("imageResId", -1)
        Log.d("FullScreenImageActivity", "Image Res ID: $imageResId")


        if (imageResId != -1) {
            imageView.setImageResource(imageResId)
        }
    }
}
