package com.clear.androidfeatures.jobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.clear.androidfeatures.R;

import java.util.ArrayList;
import java.util.ListIterator;


public class JobSchedulerActivity extends AppCompatActivity {

    private static final String TAG = JobSchedulerActivity.class.getSimpleName();

    /*** BEHAVIOR CONTROL SECTION - START ***/
    private final String MY_JOB_PARAMETER_TEXT_VALUE = "Job param sent to ";
    private final String MY_JOB_PARAMETER_BACKGROUND_EXEC_VALUE = Util.BackgroundExecutionUnit.ASYNC_TASK.toString();
    private final boolean CONTROL_CANCEL_JOBS = false;
    private final int JOB_COUNT = 4;
    private final int INITIAL_JOB_ID = 1000;
    /*** BEHAVIOR CONTROL SECTION - END ***/

    private ArrayList<JobInfo> mJobList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_scheduler);

        mJobList = new ArrayList<JobInfo>(JOB_COUNT);

        final Button button = (Button) findViewById(R.id.button_schedule_job);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button/*
                buildJobs();
                scheduleJobs();
                if (CONTROL_CANCEL_JOBS){
                    cancelJobs();
                }
            }
        });
    }

    private void buildJobs() {

        ComponentName serviceComponent = new ComponentName(getApplicationContext(),
                MyJobService.class);

        for (int i = 0; i < JOB_COUNT; i++) {

            int jobId = INITIAL_JOB_ID + i;
            JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);

            // onStartJob is called after the specified latency, no need to spin a worker thread.
            // ??? IS WHICH SITUATION IS IT NEEDED ???
            builder.setMinimumLatency(1000 * (i + 1));

            // Minimum seems to be 15min +- 5min. Lower than these are coerced to them.
            // builder.setPeriodic(1000 * 60 * 20, 1000 * 60 * 5);

            /* 1. Together with setRequiresDeviceIdle(): if used with setRequiresDeviceIdle causes
               IllegalArgumentException: An idle mode job will not respect any back-off policy, so
               calling setBackoffCriteria with setRequiresDeviceIdle is an error.

               2. Together with setRequiresCharging(): does not crash but job service is only executed
               when charger is connect so not sure on how/if setBackoffCriteria() the behavior of
               setBackoffCriteria() when used standalone.

               3. Together with setRequiredNetworkType(): same as together with setRequiresCharging().

               4. Question: setBackoffCriteria() seems to not be intended to backup the absence of any other
               criteria, but to be an independent criteria to cover job fails. Only MotoHide uses it.
               ??? If this is the case, what is the concept of a "failed job" ???
            */
            //builder.setBackoffCriteria(1000 * 5, BACKOFF_POLICY_LINEAR);
            //builder.setRequiredNetworkType(NETWORK_TYPE_NOT_ROAMING);
            //builder.setRequiresCharging(true);

            /* Idle seems to be a loose criteria, unable to trigger this in emulator even disabling
               developerOptions>stayAwake, setting unlock screen mode to swipe, pressing power and
               waiting for > 1 min
            */
            // builder.setRequiresDeviceIdle(true);

            // Send some text to be retrieved by the job.
            PersistableBundle extra = new PersistableBundle();
            extra.putString(Util.MY_JOB_PARAMETER_TEXT_KEY, MY_JOB_PARAMETER_TEXT_VALUE + "jobId: " + jobId);
            extra.putString(Util.MY_JOB_PARAMETER_BACKGROUND_EXEC_KEY, MY_JOB_PARAMETER_BACKGROUND_EXEC_VALUE);
            builder.setExtras(extra);

            JobInfo jobInfo = builder.build();

            mJobList.add(jobInfo);
        }
    }


    private void scheduleJobs(){
        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ListIterator<JobInfo> list = mJobList.listIterator();
        while (list.hasNext()){
            JobInfo jobInfo = list.next();
            js.schedule(jobInfo);
            Util.dump(getApplicationContext(), TAG, "Job scheduled: " + jobInfo.getId());
        }
    }

    private void cancelJobs(){
        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ListIterator<JobInfo> list = js.getAllPendingJobs().listIterator();
        while (list.hasNext()){
            int jobId = list.next().getId();
            js.cancel(jobId);
            Util.dump(getApplicationContext(), TAG, "Job canceled: " + jobId);
            // OBS: list creation: 1,2,3,4; list cancel order: 4,3,2,1.
        }

    }

}
