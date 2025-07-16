package com.example.smartjobtracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "job_channel",
            "Job Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you about upcoming job deadlines"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
