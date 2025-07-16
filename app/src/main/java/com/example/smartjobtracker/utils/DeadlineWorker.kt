package com.example.smartjobtracker.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartjobtracker.R

class DeadlineWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val jobTitle = inputData.getString("jobTitle") ?: "Job Reminder"
        android.util.Log.d("DeadlineWorker", "Notification triggered for job: $jobTitle")

        showNotification(jobTitle)
        return Result.success()
    }

    private fun showNotification(jobTitle: String) {
        val builder = NotificationCompat.Builder(applicationContext, "job_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Job Deadline Reminder")
            .setContentText("Deadline for $jobTitle is tomorrow!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (androidx.core.app.ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

}
