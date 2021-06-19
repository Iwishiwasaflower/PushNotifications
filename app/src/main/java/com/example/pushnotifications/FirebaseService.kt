package com.example.pushnotifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }
        Log.d("message", message.notification.toString())
        val asd = message.notification
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0, intent, FLAG_ONE_SHOT)

        // von fb console - works
        if(message.data["title"] == null){
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.notification?.title.toString())
                .setContentText(message.notification?.body.toString())
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID,notification)
        } else {
            // sich selbst schicken via firebase, does not work - crash
            Log.d("Message", message.data["message"].toString())
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID,notification)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager : NotificationManager){
        val channelName = "channelName"
        val channel =
            NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
                description = "My channel discription"
                enableLights(true)
                lightColor = Color.GREEN
            }
            notificationManager.createNotificationChannel(channel)

    }
}