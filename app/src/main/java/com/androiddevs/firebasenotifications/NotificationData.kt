package com.androiddevs.firebasenotifications

data class NotificationData(
    val title: String,
    val message: String,
    val isScheduled : Boolean,
    val scheduledTime : String
)