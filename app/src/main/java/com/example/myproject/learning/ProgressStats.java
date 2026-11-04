package com.example.myproject.learning;

import java.text.SimpleDateFormat;
import java.util.*;

public final class ProgressStats {
    public static String date(long millis){return new SimpleDateFormat("yyyy-MM-dd",Locale.ROOT).format(new Date(millis));}
    public static int streak(Set<String> dates,long now){
        Calendar day=Calendar.getInstance();day.setTimeInMillis(now);
        if(!dates.contains(date(day.getTimeInMillis())))day.add(Calendar.DAY_OF_YEAR,-1);
        int result=0;
        while(dates.contains(date(day.getTimeInMillis()))){result++;day.add(Calendar.DAY_OF_YEAR,-1);}
        return result;
    }
    public static int percent(int correct,int total){return total<=0?0:Math.max(0,Math.min(100,Math.round(correct*100f/total)));}
    private ProgressStats(){}
}
