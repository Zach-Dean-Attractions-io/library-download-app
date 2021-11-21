package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var downloadURL: String? = null
    private var downloadTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Create Notification Channel & Get Notification Manager
        notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        createNotificationChannel(CHANNEL_ID, CHANNEL_NAME)

        custom_button.setOnClickListener {
            if(downloadURL != null) {
                custom_button.changeState(ButtonState.Loading)
                download()
            } else {
                Toast.makeText(this, "Select An Option!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            id?.let { currentDownloadID ->
                if(currentDownloadID == downloadID) {

                    custom_button.changeState(ButtonState.Completed)

                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                    val queryCursor = downloadManager.query(DownloadManager.Query().setFilterById(currentDownloadID))

                    if(queryCursor.moveToFirst()) {

                        val downloadStatus = queryCursor.getInt(queryCursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        val title = queryCursor.getString(queryCursor.getColumnIndex(DownloadManager.COLUMN_TITLE))

                        // Trigger Notification
                        notificationManager.sendNotification(
                            getString(R.string.download_successful),
                            applicationContext,
                            if(downloadStatus == DownloadManager.STATUS_SUCCESSFUL) DOWNLOAD_SUCCESS else DOWNLOAD_FAILED,
                            title
                        )

                    }
                }
            }

        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(downloadURL))
                .setTitle(downloadTitle ?: "Unknown State!")
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    fun onClickRadioButton(view: View) {
        if(view is RadioButton) {
            when(view.id) {
                R.id.glide_radio_button ->  {
                    downloadURL = GLIDE_URL
                    downloadTitle = getString(R.string.glide)
                }
                R.id.loadapp_radio_button -> {
                    downloadURL = LOAD_APP_URL
                    downloadTitle = getString(R.string.loadapp)
                }
                R.id.retrofit_radio_button -> {
                    downloadURL = RETROFIT_URL
                    downloadTitle = getString(R.string.retrofit)
                }
                else -> {
                    downloadURL = null
                    downloadTitle = null
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel (
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        const val CHANNEL_ID = "library_download_channel"
        const val CHANNEL_NAME = "Library Download"

        const val DOWNLOAD_SUCCESS = "Succesful"
        const val DOWNLOAD_FAILED = "Failed"
    }

}
