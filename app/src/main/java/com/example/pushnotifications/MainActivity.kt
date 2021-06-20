package com.example.pushnotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.random.Random

//for subscribing
const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    lateinit var notificationManager : NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)



        val eTTitle : EditText = findViewById(R.id.editTextTitle)
        val eTMessage : EditText = findViewById(R.id.editTextMessage)
        val eTToken : EditText = findViewById(R.id.editTextToken)


        FirebaseService.token = FirebaseInstallations.getInstance().id.toString()
        eTToken.setText(FirebaseService.token)

        notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

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
                //TODO Toast
                Log.d("error", "no input")
            }
        }
        val sendLocal : Button = findViewById(R.id.sendDelayBtn)
        sendLocal.setOnClickListener{
            val title = eTTitle.text.toString()
            val message = eTMessage.text.toString()
            sendLocalNotification(this,title,message)
        }

    }




// Auf Antwort warten in der Coroutine -> stackoverflow ohne gehts
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            //val response =
                RetrofitInstance.api.postNotification(notification)
            //Log.d("test",response.toString())
            /*
            if(response.isSuccessful){
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
            */

        } catch (e: Exception){
            Log.e(TAG, e.toString())
        }
    }

    private fun sendLocalNotification(context: Context, title : String, message : String) = CoroutineScope(Dispatchers.IO).launch {
        //delay(5000L)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_ONE_SHOT)

        val out = NotificationCompat.Builder(context,"someName")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val notificationID = Random.nextInt()


            val channel = notificationManager.createNotificationChannel(createNotificationChannel())

            notificationManager.notify(notificationID,out)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() : NotificationChannel{
        val channelName = "channelName"
        val channel =
            NotificationChannel("someChannelName", channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Some channel discription"
                enableLights(true)
                lightColor = Color.GREEN
            }
        notificationManager.createNotificationChannel(channel)
        return channel
    }

}