package com.clear.androidfeatures.jobscheduler.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.Context;
import android.os.Message;
import android.util.Log;

/**
 * Created by josegsp on 08/01/17.
 */

public class InfiniteLoopJobService extends JobServiceBase {
    private static final String TAG = InfiniteLoopJobService.class.getSimpleName();

    public static void scheduleInfiniteLoopJob(Context ctx) {
        Log.d(TAG, "Scheduling infinite loop job.");

        JobInfo.Builder builder = getJobInfoBuilder(ctx, InfiniteLoopJobService.class,
                                                    Jobs.INFINITE_LOOP.getJobId());
        // Add a constraint indicating that the job will only run when phone is charging.
        builder.setRequiresCharging(true);

        scheduleJob(ctx, builder.build());
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        super.onStopJob(jobParameters);

        // When we return true here we're indicating that he job needs to be re-scheduled.
        // The re-schedule will happen when the FW stops the job for any reason.
        // Job will not be re-scheduled even returning true in the following cases:
        //  - cancelJob explicitly called
        //  - schedule a job with the same id
        // The minimum time to re-schedule relies on JobInfo.Builder setBackoffCriteria() that has
        // default value set to {30 seconds, exponential}
        Log.d(TAG, "Re-schedule job.");
        return true;
    }

    @Override
    public void onHandleJob(Message msg) {
        Log.d(TAG, "Handling infinite loop job service.");

        // Infinite loop to simulate the job being stopped when the JobInfo constraints
        // are not satisfied anymore while the job is running.
        while(true);
    }
}
