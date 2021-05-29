package com.abdulansari;
import com.abdulansari.SensorData;

interface IMainService {
    SensorData registerOrientationData();

    void unregisterOrientationData();
}
