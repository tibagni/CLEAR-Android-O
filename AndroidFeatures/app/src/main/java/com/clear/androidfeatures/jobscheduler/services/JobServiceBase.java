package com.clear.androidfeatures.jobscheduler.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.List;

/**
 * Created by josegsp on 7/28/17.
 */

public abstract class JobServiceBase extends JobService {

    private static final String TAG = JobServiceBase.class.getSimpleName();

    /**
     * Enum with the job IDs
     */
    public enum Jobs {
        CONNECTIVITY_CHANGES(1),
        PERIODIC(2),
        URI_CHANGES(3),
        INFINITE_LOOP(4),
        JOBINFO_BUILDER(5),
        ENQUEUE(6);

        private int mId;

        Jobs(int id) {
            mId = id;
        }

        public int getJobId() {
            return mId;
        }
    }

    // Job extra params
    public static final String EXTRA_JOB_NAME = "extra_job_name";

    // Handler Thread for the received intent actions
    private HandlerThread mHandlerThread = null;
    private JobServiceHandler mHandler;

    public class JobServiceHandler extends Handler {

        public JobServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            JobParameters jobParams = (JobParameters) msg.obj;
            int jobId = msg.what;

            Log.d(TAG, "handleMessage - JobId: " + jobId);

            onHandleJob(msg);

            jobFinished(jobParams, false);
        }
    };

    /**
     * Abstract method that should be implemented by child classes. It will be the specific
     * implementation of generic class @see{JobServiceHandler}
     *
     * @param msg
     */
    public abstract void onHandleJob(Message msg);

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mHandler = new JobServiceHandler(mHandlerThread.getLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");


        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job " + jobParameters.getJobId() + " started.");

        Message msg = mHandler.obtainMessage(jobParameters.getJobId(), jobParameters);
        mHandler.sendMessage(msg);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job " + jobParameters.getJobId() + " Stopped.");
        return false;
    }

    /**
     * Get a generic @JobInfo.Builder used to start a @JobService.
     * @param context The Context.
     * @param jobId The job id.
     * @return A @JobInfo.Builder.
     */
    protected static JobInfo.Builder getJobInfoBuilder(Context context, Class cls, int jobId) {
        Log.d(TAG, "jobId: " + jobId);

        ComponentName serviceComponent = new ComponentName(context, cls);

        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);

        return builder;
    }

    /**
     * Schedule a new job given a JobInfo.
     * If a job with the same id is already scheduled, it will be replaced by the new one.
     * IF a job with the same id is running, it will be stopped and the new one will be scheduled.
     * @param ctx The Context.
     * @param jobInfo The @JobInfo.
     */
    protected static void scheduleJob(Context ctx, JobInfo jobInfo) {
        Log.d(TAG, "schedule job - id: " + jobInfo.getId());

        JobScheduler jobScheduler =
                (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = jobScheduler.schedule(jobInfo);

        switch (result) {
            case JobScheduler.RESULT_FAILURE:
                Log.d(TAG, "Fail to schedule job");
                break;

            case JobScheduler.RESULT_SUCCESS:
                Log.d(TAG, "Success to schedule job");
                break;
        }
    }

    /**
     * Enqueue a job.
     * @param ctx The Context.
     * @param jobInfo The @JobInfo.
     * @param workItem The JobWorkItem.
     */
    protected static void enqueueJob(Context ctx, JobInfo jobInfo, JobWorkItem workItem) {
        Log.d(TAG, "enqueue job - id: " + jobInfo.getId());

        JobScheduler jobScheduler =
                (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = jobScheduler.enqueue(jobInfo, workItem);

        switch (result) {
            case JobScheduler.RESULT_FAILURE:
                Log.d(TAG, "Fail to enqueue job");
                break;

            case JobScheduler.RESULT_SUCCESS:
                Log.d(TAG, "Success to enqueue job");
                break;
        }
    }

    /**
     * Cancel a job given a job id.
     * If the job is scheduled, it will be removed from execution queue.
     * If the job is running, it will be stopped and the onJobStop() callback will be called.
     *
     * @param ctx The Context.
     * @param jobId The job id.
     */
    public static void cancelJob(Context ctx, int jobId) {
        Log.d(TAG, "cancel job - id: " + jobId);

        JobScheduler jobScheduler =
                (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }

    /**
     * Print in the log all the jobs that are scheduled or running at the moment.
     * It's not possible to differentiate if the job is scheduled or effectively running.
     * @param ctx The Context.
     */
    public static void dumpPendingJobs(Context ctx) {
        Log.d(TAG, "dump pending jobs");

        JobScheduler jobScheduler =
                (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        List<JobInfo> jobInfoList = jobScheduler.getAllPendingJobs();
        Log.d(TAG, "************************************");
        for (JobInfo info : jobInfoList) {
            Log.d(TAG, "-------------");

            Log.d(TAG, "job id: " + info.getId());

            PersistableBundle extras = info.getExtras();
            if (extras != null && extras.containsKey(EXTRA_JOB_NAME)) {
                Log.d(TAG, "job name: " + extras.getString(EXTRA_JOB_NAME));
            }

            Log.d(TAG, "-------------");
        }
        Log.d(TAG, "************************************");
    }
}
