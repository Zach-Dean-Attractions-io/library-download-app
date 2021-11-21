package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        // Get Data
        val downloadStatus = intent.getStringExtra(NotificationConstants.DOWNLOAD_STATUS)
        val downloadFileName = intent.getStringExtra(NotificationConstants.DOWNLOAD_FILE_NAME)

        file_name_value.text = downloadFileName
        status_value.text = downloadStatus

        if(downloadStatus == MainActivity.DOWNLOAD_SUCCESS) {
            status_value.setTextColor(Color.GREEN)
        } else {
            status_value.setTextColor(Color.RED)
        }

        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAllNotifications()

    }

}
