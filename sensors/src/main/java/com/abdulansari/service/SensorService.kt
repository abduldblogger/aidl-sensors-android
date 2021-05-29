package com.abdulansari.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.abdulansari.IMainService
import com.abdulansari.SensorData

class SensorService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Service onStartCommand")
        return START_STICKY
    }

    private val binder: IMainService.Stub = object : IMainService.Stub() {
        @Throws(RemoteException::class)
        override fun registerOrientationData(): SensorData {
            println("received command for registerOrientationData")
            return SensorData("testing...")
        }

        @Throws(RemoteException::class)
        override fun unregisterOrientationData() {
            println("received command for unregisterOrientationData")
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Service is stopping")
    }
}