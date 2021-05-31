package com.abdulansari.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.abdulansari.IMainService
import com.abdulansari.IMainServiceCallback
import com.abdulansari.R
import com.abdulansari.SensorData
import com.abdulansari.manager.SensorsManager


class SensorService : Service() {
    private var serviceCallback: IMainServiceCallback? = null
    private var sensorManager: SensorsManager? = null
    var handler: Handler = Handler(Looper.getMainLooper())

    private val periodicUpdate: Runnable = object : Runnable {
        override fun run() {
            handler.postDelayed(
                this,
                DELAY_IN_MILLIS
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Service onStartCommand")
        return START_STICKY
    }

    private val binder: IMainService.Stub = object : IMainService.Stub() {
        override fun registerOrientationData(callback: IMainServiceCallback?) {
            if (sensorManager == null) {
                sensorManager = SensorsManager(this@SensorService)
            }
            callback?.let {
                serviceCallback = callback
            }
            startFetchingSensorData()
        }

        override fun unregisterOrientationData() {
            stopSelf()
        }

    }

    private fun startFetchingSensorData() {
        showNotification()
        sensorManager?.startListening(object : SensorsManager.Listener {
            override fun onOrientationChanged(sensorData: SensorData) {
                serviceCallback?.onUpdateValue(sensorData)
            }
        })
        handler.post(periodicUpdate)
    }

    private fun showNotification() {
        val manager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reading data...")
            .setContentText("Reading data...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Service is stopping")
        serviceCallback = null
        handler.removeCallbacks(periodicUpdate)
        sensorManager?.stopListening()
    }

    companion object {
        private const val NOTIFICATION_ID = 3432
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val DELAY_IN_MILLIS = 8L
    }
}