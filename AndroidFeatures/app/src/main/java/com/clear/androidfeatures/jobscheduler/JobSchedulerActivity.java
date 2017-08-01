package com.clear.androidfeatures.jobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.clear.androidfeatures.R;

public class JobSchedulerActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "JobSchedulerActivity";

    private static final int NEW_MEDIA_ADDED_JOB_ID = 1;

    private Switch contentUriSwitch;
    private JobScheduler jobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_scheduler);

        contentUriSwitch = (Switch) findViewById(R.id.content_uri_switch);
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        contentUriSwitch.setChecked(isJobScheduled(NEW_MEDIA_ADDED_JOB_ID));
        contentUriSwitch.setTag(NEW_MEDIA_ADDED_JOB_ID);
        contentUriSwitch.setOnCheckedChangeListener(this);
    }

    private boolean isJobScheduled(int jobId) {
        return jobScheduler.getPendingJob(jobId) != null;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int jobId = (int) compoundButton.getTag();
        String toastStr = "Job " + getJobName(jobId);

        if (isChecked) {
            int status = jobScheduler.schedule(buildJobInfo(jobId));
            if (status == JobScheduler.RESULT_SUCCESS) {
                toastStr += " Scheduled!";
            } else {
                toastStr += " FAILED!";
                compoundButton.setChecked(false);
            }

        } else {
            jobScheduler.cancel(jobId);
            toastStr += " Canceled!";
        }

        Toast.makeText(this, toastStr, Toast.LENGTH_SHORT).show();
    }

    private JobInfo buildJobInfo(int jobId) {
        switch (jobId) {
            case NEW_MEDIA_ADDED_JOB_ID:
                return newMediaAddedJob();
        }

        return null;
    }

    private JobInfo newMediaAddedJob() {
        ComponentName jobService = new ComponentName(this, NewMediaAddedService.class);
        return new JobInfo.Builder(NEW_MEDIA_ADDED_JOB_ID, jobService)
                .addTriggerContentUri(new JobInfo.TriggerContentUri(

                        // Lets get notified when there is a new picture
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

                        // The below flag will make sure we get notified if any descendant of the
                        // URI changes as well.
                        JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS)).build();
    }

    private String getJobName(int jobId) {
        switch (jobId) {
            case NEW_MEDIA_ADDED_JOB_ID:
                return "NEW_MEDIA_ADDED_JOB";
        }

        return null;
    }
}
