package com.example.pushnotifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput

class MyNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //getting the remote input bundle from intent
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        //if there is some input
        if (remoteInput != null) {
            //getting the input value
            val msg = remoteInput.getCharSequence(ConstantResource.NOTIFICATION_REPLY)
            //updating the notification with the input value
            val mBuilder = NotificationCompat.Builder(context!!, ConstantResource.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle("Hey Thanks For Reply, ".plus(msg))
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(ConstantResource.NOTIFICATION_ID, mBuilder.build())
        }
    }
}