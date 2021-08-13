package com.androiddevs.firebasenotifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val TOPIC = "/topics/myTopic2"

class MainActivity : AppCompatActivity() {

    private var date : String? = null
    private var time : String? = null
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNumberPicker()
//
//        val alarm = Intent(this, AlertActivity::class.java)
//        startActivity(alarm)

        datePickerButton.setOnClickListener(View.OnClickListener {
            run {
                datePicker()
            }
        })
        timePickerButton.setOnClickListener(View.OnClickListener {
            run {
                timePicker()
            }
        })
        setAlarmOffline.setOnClickListener {
            run {
                offlineAlarmClick()
            }
        }
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            etToken.setText(it.token)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        btnSend.setOnClickListener {
            val title = etTitle.text.toString()
            val message = etMessage.text.toString()
            val recipientToken = etToken.text.toString()
            //val dateTime = date+" "+time
            Log.d("DEBUGGING_TAG", "${javaClass.name} title: $title, message: $message")
            val dateTime = addTimeInCurrentDate(hoursNumberPicker.value,minutesNumberPicker.value,secondsNumberPicker.value)
//            if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty() && date!=null && time!=null) {
//                PushNotification(
//                        NotificationData(title, message, true, dateTime),
//                        recipientToken
//                ).also {
//                    sendNotification(it)
//                }
//                Toast.makeText(applicationContext, "alert set for: "+date+" "+time, Toast.LENGTH_LONG).show()
//            }
            if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, message, true, dateTime),
                    recipientToken
                ).also {
                    sendNotification(it)
                }
                Toast.makeText(applicationContext, "alert set for: $dateTime", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(applicationContext, "invalid input", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
    private fun datePicker(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(it)
            date = "${calendar.get(Calendar.YEAR)}-" +
                    "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
            datePickerButton.text = date
        }
        datePicker.show(supportFragmentManager, "tag_date_picker")
    }
    private fun timePicker(){
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Appointment time")
                .build()
        picker.addOnPositiveButtonClickListener {
            time = picker.hour.toString()+":"+picker.minute.toString()+":"+"00"
            timePickerButton.text = time

        }
        picker.show(supportFragmentManager, "tag_time_picker")
    }
    private fun offlineAlarmClick(){
//        if(etTitle.text.toString().isNotEmpty() && etMessage.text.toString().isNotEmpty() && date!=null && time!=null){
//            scheduleAlarm(date+" "+time, etTitle.text.toString(),etMessage.text.toString())
//            Toast.makeText(applicationContext, "alert set for: "+date+" "+time, Toast.LENGTH_LONG).show()
//        }
        if(etTitle.text.toString().isNotEmpty() && etMessage.text.toString().isNotEmpty()){
            var date = addTimeInCurrentDate(hoursNumberPicker.value,minutesNumberPicker.value,secondsNumberPicker.value)
            Toast.makeText(applicationContext, "alert set for: $date", Toast.LENGTH_LONG).show()
            scheduleAlarm(date, etTitle.text.toString(),etMessage.text.toString())

        }
        else{
            Toast.makeText(applicationContext, "invalid input", Toast.LENGTH_LONG).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleAlarm(
        scheduledTimeString: String?,
        title: String?,
        message: String?
    ) {
        val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            Intent(applicationContext, NotificationBroadcastReceiver::class.java).let { intent ->
                intent.putExtra(ScheduledWorker.NOTIFICATION_TITLE, title)
                intent.putExtra(ScheduledWorker.NOTIFICATION_MESSAGE, message)
                PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
            }

        // Parse Schedule time
        val scheduledTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .parse(scheduledTimeString!!)
        Log.d("DEBUGGING_TAG", "notification is setting at offline: $scheduledTime")
        scheduledTime?.let {
            // With set(), it'll set non repeating one time alarm.
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                it.time,
                alarmIntent
            )
        }

    }

    private fun initNumberPicker(){
        hoursNumberPicker.maxValue = 99
        hoursNumberPicker.minValue = 0

        minutesNumberPicker.maxValue=59
        minutesNumberPicker.minValue=0

        secondsNumberPicker.maxValue=59
        secondsNumberPicker.minValue=0
    }

    private fun addTimeInCurrentDate(hours: Int, minute: Int, second: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = sdf.format(Date()).toString()
        val date:Date = sdf.parse(currentDate)
        val calender: Calendar = Calendar.getInstance()

        calender.time = date
        calender.add(Calendar.HOUR, hours)
        calender.add(Calendar.MINUTE, minute)
        calender.add(Calendar.SECOND, second)
        return sdf.format(calender.time)

    }

}
