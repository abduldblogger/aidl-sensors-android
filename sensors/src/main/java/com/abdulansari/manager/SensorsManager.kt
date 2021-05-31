package com.abdulansari.manager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.abdulansari.SensorData


internal class SensorsManager(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var sensor: Sensor? = null
    private var sensorListener: Listener? = null
    private var lastAccuracy = 0

    interface Listener {
        fun onOrientationChanged(sensorData: SensorData)
    }

    init {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    fun startListening(listener: Listener) {
        sensorListener = listener
        if (sensor == null) {
            println("Rotation vector sensor not available; will not provide orientation data.");
        } else {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
        sensorListener = null
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        lastAccuracy = accuracy
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (sensorListener == null) {
            return
        }
        if (lastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return
        }
        if (event?.sensor == sensor) {
            event?.values?.let {
                updateOrientation(event.values)
            }
        }
    }

    private fun updateOrientation(rotationVector: FloatArray?) {
        println(rotationVector)
        val sensorObj: SensorData = getSensorData(rotationVector)
        sensorListener?.onOrientationChanged(sensorObj)
    }

    private fun getSensorData(rotationVector: FloatArray?): SensorData {
        if (rotationVector?.isNotEmpty() == true && rotationVector.size >= 3) {
            return SensorData(
                pitch = rotationVector[0],
                roll = rotationVector[1],
                azimuth = rotationVector[2]
            )
        }
        val default = 0f
        return SensorData(
            pitch = default,
            roll = default,
            azimuth = default
        )
    }
}