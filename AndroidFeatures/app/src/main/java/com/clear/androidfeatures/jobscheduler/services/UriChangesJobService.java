package com.clear.androidfeatures.jobscheduler.services;

import android.app.job.JobInfo;
import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.util.Log;

/**
 * Created by josegsp on 7/31/17.
 */

public class UriChangesJobService extends JobServiceBase {
    private static final String TAG = UriChangesJobService.class.getSimpleName();

    public static void scheduleUriChangesJob(Context ctx, Uri uri) {
        Log.d(TAG, "Scheduling periodic job.");

        JobInfo.Builder builder = getJobInfoBuilder(ctx, UriChangesJobService.class,
                                                    Jobs.URI_CHANGES.getJobId());
        builder.addTriggerContentUri(new JobInfo.TriggerContentUri(uri,
                                            JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));

        scheduleJob(ctx, builder.build());
    }


    @Override
    public void onHandleJob(Message msg) {
        Log.d(TAG, "Handling Uri changed job service.");
    }
}
