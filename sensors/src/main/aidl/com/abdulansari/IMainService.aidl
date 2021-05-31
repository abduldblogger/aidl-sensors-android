package com.abdulansari;
import com.abdulansari.IMainServiceCallback;

interface IMainService {
    void registerOrientationData(IMainServiceCallback callback);

    void unregisterOrientationData();
}
