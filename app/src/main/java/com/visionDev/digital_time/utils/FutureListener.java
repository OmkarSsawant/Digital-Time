package com.visionDev.digital_time.utils;

public interface FutureListener<T>{
    void onSuccess(T result);
    void onFailure(Exception e);
}
