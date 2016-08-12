package com.jcz.myapplication.downloadUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by asus on 2016/8/11.
 */
public class TimerUtils {


    private static Timer timer;
    private static TimerTask timerTask;

    public interface OnTimerTaskRunning{
        void onTimerTaskRunning();
    }

    public static void startTimer(long delay,long period,final OnTimerTaskRunning running){

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (running!=null){
                    running.onTimerTaskRunning();
                }
            }
        };

        timer.schedule(timerTask,delay,period);
    }


    public static void cancelTimer(){
        if (timer!=null&&timerTask!=null){
            timer.cancel();
            timerTask.cancel();
        }
    }


}
