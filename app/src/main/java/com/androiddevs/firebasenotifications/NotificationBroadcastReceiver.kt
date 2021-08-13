package com.androiddevs.firebasenotifications

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.androiddevs.firebasenotifications.ScheduledWorker.Companion.NOTIFICATION_MESSAGE
import androidx.work.Data
import android.content.BroadcastReceiver

import com.androiddevs.firebasenotifications.ScheduledWorker.Companion.NOTIFICATION_TITLE
import java.text.SimpleDateFormat
import java.util.*

class NotificationBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val title = it.getStringExtra(NOTIFICATION_TITLE)
            val message = it.getStringExtra(NOTIFICATION_MESSAGE)
            Log.d("DEBUGGING_TAG", "${javaClass.name} title: $title, message: $message")

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentDate = sdf.format(Date()).toString()
            Log.d("DEBUGGING_TAG", "alarm bell$currentDate")

            // Create Notification Data
            val notificationData = Data.Builder()
                .putString(NOTIFICATION_TITLE, title)
                .putString(NOTIFICATION_MESSAGE, message)
                .build()

            // Init Worker
            val work = OneTimeWorkRequest.Builder(ScheduledWorker::class.java)
                .setInputData(notificationData)
                .build()

            // Start Worker
            WorkManager.getInstance().beginWith(work).enqueue()

            Log.d(javaClass.name, "WorkManager is Enqueued.")
        }
    }
}