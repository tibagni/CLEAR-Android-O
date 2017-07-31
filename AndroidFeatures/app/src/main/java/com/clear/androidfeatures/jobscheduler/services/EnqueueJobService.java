package com.clear.androidfeatures.jobscheduler.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobWorkItem;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

/**
 * Created by josegsp on 7/31/17.
 */

public class EnqueueJobService extends JobServiceBase {
    private static final String TAG = EnqueueJobService.class.getSimpleName();

    private static final String ENQUEUE_ORDER = "enqueue_order";
    private static int mEnqueueOrder = 0;

    public static void enqueueJobService(Context ctx) {
        Log.d(TAG, "Enqueue job.");

        JobInfo.Builder builder = getJobInfoBuilder(ctx, EnqueueJobService.class,
                                                    Jobs.ENQUEUE.getJobId());
        /*
        NETWORK_TYPE_NONE,
        NETWORK_TYPE_ANY,
        NETWORK_TYPE_UNMETERED,
        NETWORK_TYPE_NOT_ROAMING,
        NETWORK_TYPE_METERED.
        */
        builder.setMinimumLatency(2000);
        builder.setRequiresCharging(true);
        builder.setBackoffCriteria(5000, JobInfo.BACKOFF_POLICY_LINEAR);

        mEnqueueOrder++;
        Intent i = new Intent();
        i.putExtra(ENQUEUE_ORDER, mEnqueueOrder);

        /**
         * We can't enqueue persisted jobs.
         * We can't enqueue jobs if (JobWorkItem == null)
         *
         */

        enqueueJob(ctx, builder.build(), new JobWorkItem(i));
    }


    @Override
    public void onHandleJob(Message msg) {
        Log.d(TAG, "Handling enqueue job service.");

        JobParameters params = (JobParameters) msg.obj;

        JobWorkItem workItem = null;
        while ((workItem = params.dequeueWork()) != null) {
            Log.d(TAG, "Deliver count: " + workItem.getDeliveryCount());
            Log.d(TAG, "Enqueue order: " + workItem.getIntent().getIntExtra(ENQUEUE_ORDER, -1));
            mEnqueueOrder--;
        }

        Log.d(TAG, "Infinite loop...");
        while (true);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        super.onStopJob(jobParameters);
        Log.d(TAG, "Returning true to re-schedule.");

        mEnqueueOrder = 0;

        return true;
    }
}
