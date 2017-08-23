package com.clear.androidfeatures.jobscheduler.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;

import java.net.URI;

/**
 * Created by josegsp on 7/31/17.
 */

public class JobInfoBuilderTestsJobService extends JobServiceBase {
    private static final String TAG = JobInfoBuilderTestsJobService.class.getSimpleName();
    private static final String JOB_NAME = "JobInfo.Builder tests job.";

    private static final long JOB_INTERVAL = 1000*60*15;    // 15s
    private static final Uri JOB_URI = ContactsContract.RawContacts.CONTENT_URI;
    private static final Uri JOB_CLIPDATA_URI = Uri.parse("content://com.clear.androidfeatures");

    public static void scheduleJobInfoBuilderTests(Context ctx) {
        Log.d(TAG, "Scheduling JobInfo.Builder tests job.");

        JobInfo.Builder builder = getJobInfoBuilder(ctx, JobInfoBuilderTestsJobService.class,
                                                    Jobs.JOBINFO_BUILDER.getJobId());

        // JobInfo.Builder constraints
//        builder.addTriggerContentUri(new JobInfo.TriggerContentUri(JOB_URI,
//                JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
//
//        builder.setPeriodic(JOB_INTERVAL);
//        builder.setPeriodic(JOB_INTERVAL, (long) (JOB_INTERVAL*0.1));

//        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);

        builder.setBackoffCriteria(10, JobInfo.BACKOFF_POLICY_LINEAR/*JobInfo.BACKOFF_POLICY_EXPONENTIAL*/);

//        builder.setClipData(ClipData.newUri(ctx.getContentResolver(), "Clip Data", JOB_CLIPDATA_URI),
//                                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

//        builder.setMinimumLatency(1000);

//        // Set the maximum time that job needs to wait before executing, even if
//        // the constraints were not satisfied.
        builder.setOverrideDeadline(0);

        builder.setPersisted(false/*true*/);

//        builder.setRequiresBatteryNotLow();
//        builder.setRequiresCharging();
//        builder.setRequiresDeviceIdle();
//        builder.setRequiresStorageNotLow();

        Bundle b = new Bundle();
        b.putString(EXTRA_JOB_NAME, JOB_NAME);
        builder.setTransientExtras(b);

//        builder.setTriggerContentMaxDelay(500);

//        builder.setTriggerContentUpdateDelay(500);

        // add job name
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString(EXTRA_JOB_NAME, JOB_NAME);
        builder.setExtras(bundle);


        scheduleJob(ctx, builder.build());
    }

    @Override
    public void onHandleJob(Message msg) {
        Log.d(TAG, "Handling JobInfo.Builder tests service.");

        JobParameters jobParams = (JobParameters) msg.obj;

        // Handle ClipData
        ClipData clipData = (ClipData) jobParams.getClipData();
        if (clipData != null) {
            for (int i=0; i<clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Log.d(TAG, "Clip data text: " + item.getText());
                Log.d(TAG, "Clip data uri: " + item.getUri());
            }
        }

        // Handle Bundle
        Bundle b = (Bundle) jobParams.getTransientExtras();
        if (b != null) {
            Log.d(TAG, "Received bundle:");

            for (String key : b.keySet()) {
                Log.d(TAG, "Key: " + key + " - Value: " + b.get(key));
            }
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        boolean reSchedule = true;

        Log.d(TAG, "Job " + jobParameters.getJobId() + " Stopped. Re-Schedu: " + reSchedule);

        return reSchedule;
    }
}