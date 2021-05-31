package com.abdulansari

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import com.abdulansari.constant.LibConstants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IMainServiceCallback {
    private var service: IMainService? = null
    private var binder: IBinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindAndStartService()
    }

    private fun bindAndStartService() {
        val serviceIntent = Intent()
            .setComponent(
                ComponentName(
                    LibConstants.SERVICE_PACKAGE_NAME,
                    LibConstants.SERVICE_CLASS_NAME
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
            this@MainActivity.binder = binder
            println("Service binded")
            service = IMainService.Stub.asInterface(binder)
            populateData()
        }
    }

    private fun populateData() {
        try {
            service?.registerOrientationData(this)
        } catch (re: RemoteException) {
            println(re.printStackTrace())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        service?.unregisterOrientationData()
    }

    override fun asBinder(): IBinder? {
        return binder
    }

    override fun onUpdateValue(sensorData: SensorData) {
        activity_main_tv_sensor_data?.text =
            "Rotation vector data: pitch -> ${sensorData.pitch} \n " +
                    "roll -> ${sensorData.roll} \n azimuth-> ${sensorData.azimuth}"
    }
}