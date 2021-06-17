package com.androiddevs.firebasenotifications

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alert.*
import java.util.*


class AlertActivity : AppCompatActivity() {
    private var title: String?=null
    private var message: String?=null

    private var mediaPlayer: MediaPlayer?=null
    private lateinit var r: Ringtone
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    lateinit var pendingIntent: PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        title=""
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        getExtra()
        if(title!=null){
            titleTextView.text = title
            messageTextView.text = message
        }




//        btnStart = findViewById(R.id.btnStartService)
//        btnStop = findViewById(R.id.btnStopService)

//        val myIntent = Intent(this@AlertActivity, MyAlarmService::class.java)
//        pendingIntent = PendingIntent.getService(this@AlertActivity, 0, myIntent, 0)
//        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val calendar: Calendar = Calendar.getInstance()
//        calendar.timeInMillis = System.currentTimeMillis()
//        calendar.add(Calendar.SECOND, 3)
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//        Toast.makeText(baseContext, "Starting Service Alarm", Toast.LENGTH_LONG).show()

//        btnStop.setOnClickListener {
//            val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            alarmManager.cancel(pendingIntent)
//            Toast.makeText(baseContext, "Service Cancelled", Toast.LENGTH_LONG).show()
//        }
        //showDialog()
        this.setFinishOnTouchOutside(false);
        //playAlertSound()
        beepPlay()
        stop_button.setOnClickListener {
            run {
                try {
                    //r.stop()
                        mediaPlayer?.stop()
                    finish()
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun beepPlay(){
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        mediaPlayer?.setOnPreparedListener{

        }
        mediaPlayer?.isLooping=true
        mediaPlayer?.start()

    }


    private fun playAlertSound(){
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()    // Do nothing or catch the keys you want to block

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getExtra(){
        try {
            title = intent.getStringExtra("title")
            message = intent.getStringExtra("message")
        }
        catch (e:Exception){

        }
    }

    override fun onBackPressed() {

    }


}