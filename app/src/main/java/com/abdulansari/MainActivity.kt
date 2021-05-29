package com.abdulansari

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var service: IMainService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindAndStartService()
    }

    private fun bindAndStartService() {
        val serviceIntent = Intent()
            .setComponent(
                ComponentName(
                    "com.abdulansari",
                    "com.abdulansari.service.SensorService"
                )
            )
        println("Binding service…")
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(componentName: ComponentName?) {
            println("Service disconnected")
            service = null
        }

        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            println("Service binded")
            service = IMainService.Stub.asInterface(binder)
            populateData()
        }
    }

    private fun populateData() {
        try {
            val data = service?.registerOrientationData()
            activity_main_tv_sensor_data?.text = data?.orientation
        } catch (re: RemoteException) {
            println(re.printStackTrace())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        service?.unregisterOrientationData()
    }
}