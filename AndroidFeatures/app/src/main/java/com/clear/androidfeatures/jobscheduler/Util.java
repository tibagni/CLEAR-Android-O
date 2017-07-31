package com.clear.androidfeatures.jobscheduler;

import android.content.Context;
import android.widget.Toast;
import android.util.Log;

/**
 * Created by graciosa on 7/26/17.
 */

public class Util {

    public enum BackgroundExecutionUnit{
        THREAD,
        ASYNC_TASK
    }

    public static final String MY_JOB_PARAMETER_TEXT_KEY = "my_job_parameters_key";
    public static final String MY_JOB_PARAMETER_BACKGROUND_EXEC_KEY = "my_job_bg_exec_key";

    private static final boolean LOG = true;
    private static final boolean TOAST = true;

    public static void dump (Context context, String tag, String text){
        if (LOG) {log(tag,text);}
        if (TOAST) {toast(context,text);}
    }

    private static void toast(Context context, String text){
        //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private static void log (String tag, String text){
        Log.d(tag,text);
    }

}
