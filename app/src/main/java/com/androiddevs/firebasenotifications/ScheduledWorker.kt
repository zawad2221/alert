package com.androiddevs.firebasenotifications

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters


class ScheduledWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        Log.d(TAG, "Work START")

        // Get Notification Data
        val title = inputData.getString(NOTIFICATION_TITLE)
        val message = inputData.getString(NOTIFICATION_MESSAGE)
        Log.d("DEBUGGING_TAG", "${javaClass.name} title: $title, message: $message")

        // Show Notification
        val alarm = Intent(applicationContext, AlertActivity::class.java)
        alarm.putExtra("title",title)
        alarm.putExtra("message",message)
        alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(alarm)

        // TODO Do your other Background Processing

        Log.d(TAG, "Work DONE")
        // Return result

        return Result.success()
    }

    companion object {
        private const val TAG = "ScheduledWorker"
        const val NOTIFICATION_TITLE = "notification_title"
        const val NOTIFICATION_MESSAGE = "notification_message"
    }
}
