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
    private static final String CALL_COMPLETE = "call_complete";

    private static final int MAX_WORK_ITEMS = 5;
    private static final int DELAY_BEFORE_FINISH = 10000;
    private static int sEnqueueOrder = 0;

    public static void enqueueJobService(Context ctx) {
        Log.d(TAG, "Enqueue job.");

        JobInfo jobInfo = getJobInfo(ctx);
        for (int i=0; i<MAX_WORK_ITEMS; i++) {
            sEnqueueOrder++;
            Intent intent = getIntent(sEnqueueOrder);
            enqueueJob(ctx, jobInfo, new JobWorkItem(intent));
        }

    }

    public static void enqueueJobServiceWithoutComplete(Context ctx) {
        Log.d(TAG, "Enqueue job that will not be completed.");

        JobInfo jobInfo = getJobInfo(ctx);
        for (int i=0; i<MAX_WORK_ITEMS; i++) {
            sEnqueueOrder++;
            Intent intent = getIntent(sEnqueueOrder);
            intent.putExtra(CALL_COMPLETE, false);
            enqueueJob(ctx, jobInfo, new JobWorkItem(intent));
        }
    }

    private static JobInfo getJobInfo(Context ctx) {
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

        return builder.build();
    }

    private static Intent getIntent(int queueOrder) {
        Intent i = new Intent();
        i.putExtra(ENQUEUE_ORDER, queueOrder);

        return i;
    }

    /**
     * Convenience method for enqueuing work in to this service and sinalize that we don't want
     * to call work completed.
     * The purpose is to emulate
     */
    public static void enqueueWithoutCompleting(Context context) {
        Log.d(TAG, "Enqueue work.");


    }

    @Override
    public void onHandleJob(Message msg) {
        Log.d(TAG, "Handling enqueue job service.");

        JobParameters params = (JobParameters) msg.obj;

        JobWorkItem workItem = null;
        while ((workItem = params.dequeueWork()) != null) {
            Log.d(TAG, "Deliver count: " + workItem.getDeliveryCount());

            Intent i = workItem.getIntent();
            int order = i.getIntExtra(ENQUEUE_ORDER, -1);
            boolean completeWork = i.getBooleanExtra(CALL_COMPLETE, true);

            Log.d(TAG, "Job work order: " + order);
            if (completeWork) {
                Log.d(TAG, "Complete job work: " + order);
                params.completeWork(workItem);
            } else {
                Log.d(TAG, "Job work: " + order + " not completed.");
            }
        }

        Log.d(TAG, "Waiting " + DELAY_BEFORE_FINISH + " to finish handling the job.");
        try {
            Thread.sleep(DELAY_BEFORE_FINISH);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Job handle finished.");
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        super.onStopJob(jobParameters);
        Log.d(TAG, "Returning true to re-schedule.");

        return true;
    }
}
