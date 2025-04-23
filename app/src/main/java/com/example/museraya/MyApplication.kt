package com.example.museraya

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MyApplication : Application() {

    companion object {
        // Use the ID defined in strings.xml (or hardcode here if you prefer)
        const val CHANNEL_ID_GENERAL = "museraya_channel_general"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        // Create channel only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define channel properties (use string resources)
            val name = getString(R.string.channel_name_general) // Add this string
            val descriptionText = getString(R.string.channel_description_general) // Add this string
            val importance = NotificationManager.IMPORTANCE_HIGH // Use HIGH to make it pop up

            // Create the channel object
            val channel = NotificationChannel(CHANNEL_ID_GENERAL, name, importance).apply {
                description = descriptionText
                // Optional: Configure lights, vibration, etc.
                // enableLights(true)
                // lightColor = Color.RED // Use your app's color
                // enableVibration(true)
                // vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}