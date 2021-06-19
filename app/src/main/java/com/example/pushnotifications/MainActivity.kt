package com.example.pushnotifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

//for subscribing
const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        val eTTitle : EditText = findViewById(R.id.editTextTitle)
        val eTMessage : EditText = findViewById(R.id.editTextMessage)
        val eTToken : EditText = findViewById(R.id.editTextToken)

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
            }
        }
    }





    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception){
            Log.e(TAG, e.toString())
        }
    }
}