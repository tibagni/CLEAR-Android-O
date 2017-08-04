package com.clear.androidfeatures.jobscheduler.activities;

import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.clear.androidfeatures.R;
import com.clear.androidfeatures.jobscheduler.services.ConnectivityChangesJobService;
import com.clear.androidfeatures.jobscheduler.services.InfiniteLoopJobService;
import com.clear.androidfeatures.jobscheduler.services.JobServiceBase;
import com.clear.androidfeatures.jobscheduler.services.PeriodicJobService;
import com.clear.androidfeatures.jobscheduler.services.UriChangesJobService;

public class JobSchedulerActivity extends AppCompatActivity {

    private static final long JOB_INTERVAL = 1000*60*15;    // 15s
    private static final Uri JOB_URI = ContactsContract.RawContacts.CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_scheduler);

        Button jobPeriodic = (Button) findViewById(R.id.job_periodic_schedule);
        jobPeriodic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeriodicJobService.schedulePeriodicJob(getBaseContext(), JOB_INTERVAL);
            }
        });
        Button jobPeriodicCancel = (Button) findViewById(R.id.job_periodic_cancel);
        jobPeriodicCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeriodicJobService.cancelJob(getBaseContext(),
                                                JobServiceBase.Jobs.PERIODIC.getJobId());
            }
        });

        Button jobConnectivity = (Button) findViewById(R.id.job_connectivity_schedule);
        jobConnectivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityChangesJobService.scheduleConnChangesJob(getBaseContext());
            }
        });

        Button jobUriChanges = (Button) findViewById(R.id.job_uri_schedule);
        jobUriChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UriChangesJobService.scheduleUriChangesJob(getBaseContext(), JOB_URI);
            }
        });

        Button infiniteLoop = (Button) findViewById(R.id.job_infinite_loop_schedule);
        infiniteLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfiniteLoopJobService.scheduleInfiniteLoopJob(getBaseContext());
            }
        });

        Button dumpPendingJobs = (Button) findViewById(R.id.dump_pending_jobs);
        dumpPendingJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JobServiceBase.dumpPendingJobs(getBaseContext());
            }
        });
    }
}
