package com.project.trafficpulse.Response

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat

class NotificationService : Service() {
    private val roadSafetyMessages = arrayListOf(
        "Wet roads ahead, drive carefully.",
        "Speed limit: 60 km/h in this zone.",
        "Slippery road, reduce speed.",
        "Foggy conditions, use headlights.",
        "Road curves ahead, maintain control.",
        "School zone nearby, slow down.",
        "Sharp turn ahead, caution advised.",
        "Steep hill ahead, prepare to shift gears.",
        "Reduce speed, pedestrian crossing.",
        "Icy conditions possible, drive safely.",
        "Stay in lane, narrow road ahead.",
        "Road narrows, reduce speed.",
        "Animal crossing area, be alert.",
        "Drive carefully, loose gravel on road.",
        "Heavy winds reported, hold steering firmly.",
        "Dusty road ahead, drive cautiously.",
        "Speed limit: 80 km/h for the next 5 km.",
        "Bumpy road ahead, reduce speed.",
        "Highway merge ahead, watch for vehicles.",
        "Lane ends in 500 meters, prepare to merge.",
        "Caution: wet leaves on road.",
        "Stay cautious: road surface may be uneven.",
        "New road markings, follow carefully.",
        "Bridge ahead, adjust speed.",
        "High temperature zone, check tires.",
        "Tunnel ahead, turn on headlights.",
        "Residential area, speed limit: 30 km/h.",
        "Yield to oncoming traffic ahead.",
        "Be aware: road surface may be slippery.",
        "Expect slow traffic during peak hours.",
        "Steep descent, reduce speed.",
        "No passing zone for the next 2 km.",
        "Roadwork ahead, lane shift in place.",
        "Right lane closed in 1 km, merge left.",
        "Advisory: drive cautiously in this area.",
        "Construction zone ahead, reduce speed.",
        "Caution: narrow bridge.",
        "Parking zone nearby, stay alert for cars.",
        "Mountain pass ahead, drive cautiously.",
        "Farm vehicles may be entering roadway.",
        "Speed check area ahead, maintain limit.",
        "Stay focused: high pedestrian area.",
        "Reduce speed: winding road.",
        "No stopping zone for the next 1 km.",
        "Emergency vehicles may be active in this area.",
        "Watch for cyclists on the road.",
        "Slow down: high risk of wildlife crossing.",
        "Turn on fog lights in this area.",
        "Approaching intersection, yield to traffic.",
        "Reduce speed: high traffic congestion."
    )

    private var messageIndex = 0
    private lateinit var handler: Handler
    private lateinit var notificationRunnable: Runnable

    override fun onCreate() {
        super.onCreate()
        handler = Handler()
        createNotificationChannel()


        notificationRunnable = object : Runnable {
            override fun run() {
                if (messageIndex < roadSafetyMessages.size) {
                    sendNotification(roadSafetyMessages[messageIndex])
                    messageIndex++
                } else {
                    messageIndex = 0
                }
                handler.postDelayed(this, 180000)  // 3 minutes delay
            }
        }


        handler.post(notificationRunnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(notificationRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun sendNotification(message: String) {
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification: Notification = NotificationCompat.Builder(this, "ROAD_SAFETY_CHANNEL")
            .setContentTitle("Road Safety Alert")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 1000))
            .setLights(Color.RED, 3000, 3000)
            .setAutoCancel(true)  // Dismiss when clicked
            .setFullScreenIntent(null, true)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.deleteNotificationChannel("ROAD_SAFETY_CHANNEL")

            val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val channel = NotificationChannel(
                "ROAD_SAFETY_CHANNEL",
                "Road Safety Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for road safety notifications"
                setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT)
                enableVibration(true)
                enableLights(true)
                lightColor = Color.RED
            }

            notificationManager?.createNotificationChannel(channel)
        }
    }
}



