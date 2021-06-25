package com.example.pushnotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.random.Random

//for subscribing
const val TOPIC = "/topics/Topic"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        createNotificationChannel()

        val eTTitle : EditText = findViewById(R.id.editTextTitle)
        val eTMessage : EditText = findViewById(R.id.editTextMessage)


        val sendBtn : Button = findViewById(R.id.sendButton)
        sendBtn.setOnClickListener{
            val title = eTTitle.text.toString()
            val message = eTMessage.text.toString()
            if(title.isNotEmpty() && message.isNotEmpty()){
                PushNotification(
                    NotificationData(title,message), TOPIC
                ).also {
                    sendNotification(it)
                }
            }else {
                    Toast.makeText(this,"Please set a Title and Message",Toast.LENGTH_LONG).show()
                Log.d("error", "no input")
            }
        }


        val sendLocal : Button = findViewById(R.id.sendDelayBtn)
        sendLocal.setOnClickListener{
            val title = eTTitle.text.toString()
            val message = eTMessage.text.toString()
            val notification = NotificationCompat.Builder(this,"channelID")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            val notificationManager  =  NotificationManagerCompat.from(this)
            val notificationID = Random.nextInt()

            notificationManager.notify(notificationID,notification)
        }
    }





    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
                RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception){
            Log.e(TAG, e.toString())
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("channelID","channelName",NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Some channel discription"
                enableLights(true)
                lightColor = Color.GREEN
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

}