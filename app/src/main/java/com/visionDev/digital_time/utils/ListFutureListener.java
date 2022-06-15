package com.visionDev.digital_time.utils;

import java.util.List;

public interface ListFutureListener<T>{
    void onSuccess(List<T> result);
    void onFailure(Exception e);
}
