package com.abdulansari

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SDKAidl.SensorCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SDKAidl.init(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        SDKAidl.unregisterOrientationData()
    }

    override fun onValueReceived(sensorData: SensorData) {
        activity_main_tv_sensor_data?.text =
            "Rotation vector data: pitch -> ${sensorData.pitch} \n " +
                    "roll -> ${sensorData.roll} \n azimuth-> ${sensorData.azimuth}"
    }
}