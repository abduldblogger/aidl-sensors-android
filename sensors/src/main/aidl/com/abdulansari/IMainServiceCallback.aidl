package com.abdulansari;
import com.abdulansari.SensorData;

// Declare any non-default types here with import statements

interface IMainServiceCallback {
    void onUpdateValue(in SensorData sensorData);
}