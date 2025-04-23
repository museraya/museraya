package com.example.museraya // Or your utils package name

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationUtils { // Make sure it's an object

    const val NOTIFICATION_ID_BOOKING_STATUS = 1001 // Base ID

    fun showSimpleNotification(
        context: Context,
        channelId: String,
        notificationId: Int, // Make sure ID is unique per notification shown
        title: String,
        message: String,
        intent: Intent? = null
    ) {
        // --- Permission Check (API 33+) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                println("Notification permission not granted. Cannot show notification.")
                return // Exit
            }
        }

        val pendingIntent: PendingIntent? = intent?.let {
            PendingIntent.getActivity(
                context,
                notificationId, // Use unique request code per notification
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo1) // **** ENSURE THIS ICON EXISTS ****
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Changed to HIGH for pop-up
            .setAutoCancel(true)
            .apply {
                setContentIntent(pendingIntent)
            }

        val notificationManager = NotificationManagerCompat.from(context)

        // Ensure channel exists (though primary creation is elsewhere)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                println("Error: Notification channel $channelId not found before notify!")
                // Avoid creating channel here, rely on MyApplication/Activity
            }
        }

        // Notify with the unique ID
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Catch potential SecurityException if permission check somehow fails race condition
            println("SecurityException showing notification: ${e.message}")
        }
    }
}