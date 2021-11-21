package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private val NOTIFICATION_ID = 0

object NotificationConstants {
    const val DOWNLOAD_STATUS = "download_status"
    const val DOWNLOAD_FILE_NAME = "download_file_name"
}

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    downloadStatus: String,
    downloadFileName: String
) {

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)

    detailIntent.putExtra(NotificationConstants.DOWNLOAD_STATUS, downloadStatus)
    detailIntent.putExtra(NotificationConstants.DOWNLOAD_FILE_NAME, downloadFileName)

    val detailPendingIndent = PendingIntent.getActivity(
        applicationContext, NOTIFICATION_ID, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(MainActivity.CHANNEL_NAME)
        .setContentIntent(detailPendingIndent)
        .setAutoCancel(true)
        .setContentText(messageBody)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(
            R.drawable.ic_assistant_black_24dp, applicationContext.getString(R.string.notification_button), detailPendingIndent
        )

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelAllNotifications() {
    cancelAll()
}