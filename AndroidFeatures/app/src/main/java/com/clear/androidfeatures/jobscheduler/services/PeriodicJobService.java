package com.clear.androidfeatures.jobscheduler.services;

import android.app.job.JobInfo;
import android.content.Context;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;

/**
 * Created by josegsp on 7/31/17.
 */

public class PeriodicJobService extends JobServiceBase {
    private static final String TAG = PeriodicJobService.class.getSimpleName();
    private static final String JOB_NAME = "Periodic job.";

    public static void schedulePeriodicJob(Context ctx, long interval) {
        Log.d(TAG, "Scheduling periodic job.");

        JobInfo.Builder builder = getJobInfoBuilder(ctx, PeriodicJobService.class,
                                                    Jobs.PERIODIC.getJobId());
        //
        builder.setPeriodic(interval);

        // add job name
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString(EXTRA_JOB_NAME, JOB_NAME);
        builder.setExtras(bundle);


        scheduleJob(ctx, builder.build());
    }

    @Override
    public void onHandleJob(Message msg) {
        Log.d(TAG, "Handling periodic job service.");
    }
}
