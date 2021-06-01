package com.abdulansari

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException

object SDKAidl : IMainServiceCallback {
    private const val SERVICE_PACKAGE_NAME = "com.abdulansari"
    private const val SERVICE_CLASS_NAME = "com.abdulansari.service.SensorService"

    private var service: IMainService? = null
    private var binder: IBinder? = null
    private var sensorCallback: SensorCallback? = null

    interface SensorCallback {
        fun onValueReceived(sensorData: SensorData)
    }

    fun init(context: Context, callback: SensorCallback) {
        sensorCallback = callback
        val serviceIntent = Intent()
            .setComponent(
                ComponentName(
                    SERVICE_PACKAGE_NAME,
                    SERVICE_CLASS_NAME
                )
            )
        println("Binding service…")
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(componentName: ComponentName?) {
            println("Service disconnected")
            service = null
        }

        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            this@SDKAidl.binder = binder
            println("Service binded")
            service = IMainService.Stub.asInterface(binder)
            registerOrientationData()
        }
    }

    private fun registerOrientationData() {
        try {
            service?.registerOrientationData(this)
        } catch (re: RemoteException) {
            println(re.printStackTrace())
        }
    }

    fun unregisterOrientationData() {
        sensorCallback = null
        service?.unregisterOrientationData()
    }

    override fun onUpdateValue(sensorData: SensorData) {
        sensorCallback?.onValueReceived(sensorData)
    }

    override fun asBinder(): IBinder? {
        return binder
    }
}