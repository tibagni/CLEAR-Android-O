package com.clear.androidfeatures.jobscheduler.services;

import android.app.job.JobInfo;
import android.content.Context;
import android.os.Message;
import android.util.Log;

/**
 * Created by josegsp on 7/31/17.
 */

public class ConnectivityChangesJobService extends JobServiceBase {
    private static final String TAG = ConnectivityChangesJobService.class.getSimpleName();

    public static void scheduleConnChangesJob(Context ctx) {
        Log.d(TAG, "Scheduling connectivity changes job.");

        JobInfo.Builder builder = getJobInfoBuilder(ctx, ConnectivityChangesJobService.class,
                                                    Jobs.CONNECTIVITY_CHANGES.getJobId());
        /*
        NETWORK_TYPE_NONE,
        NETWORK_TYPE_ANY,
        NETWORK_TYPE_UNMETERED,
        NETWORK_TYPE_NOT_ROAMING,
        NETWORK_TYPE_METERED.
        */
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);

        scheduleJob(ctx, builder.build());
    }


    @Override
    public void onHandleJob(Message msg) {
        Log.d(TAG, "Handling connectivity changes job service.");
    }
}
