package com.example.quotevault.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object DailyQuoteNotification {

    const val CHANNEL_ID = "daily_quote_channel"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Quote",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily quote notification"
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
