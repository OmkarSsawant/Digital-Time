package com.visionDev.digital_time.utils;

import java.util.Calendar;
import java.util.Date;

public class UsageStatsUtils {
    public  static long getTodayDayStart(){
       Date today=  Calendar.getInstance().getTime();
             Calendar calendar =   Calendar.getInstance();
                            calendar.setTime(today);
                            calendar.set(Calendar.HOUR_OF_DAY,0);
                            calendar.set(Calendar.MINUTE,0);
                            calendar.set(Calendar.SECOND,0);
      return  calendar.getTime().getTime();
    }
}
