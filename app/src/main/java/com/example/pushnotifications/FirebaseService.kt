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
//Channels which the App listens to.
//For better user controlling which notifications he wants to ignore/see
//For additional Settings like Sound/Receive type.
private const val CHANNEL_ID = "my_channel"
private const val CHANNEL_NAME = "Channel_IGNORE"


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService : FirebaseMessagingService() {

    /**
     * implements the interface for receiving messages from extern services.
     * Mainly used as Service is Google Firebase.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        //The Communication to an Activity works with intent.
        //Explicit Intent is a messaging object used to request an action from another app component.
        val intent = Intent(this, MainActivity::class.java)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        Log.d("message", message.notification.toString())

        //That the activity we intent to perform an action is already running and should be now the "new actual activity"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //This declares that we the intent is a one time object we wont use it another time.
        val pendingIntent = PendingIntent.getActivity(this,0, intent, FLAG_ONE_SHOT)

        // von fb console - works
        if(message.data["title"] == null){
            //Design of the Notification
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
    /**
     * This Methode creates the Notification channel on which the Notification gets send.
     * The purpose is, to have more Settings options for the user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager : NotificationManager){
        //How it should be delivered.
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH).apply {
                description = "My channel discription"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
    }
}