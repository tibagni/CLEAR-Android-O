package com.clear.androidfeatures.jobscheduler.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.clear.androidfeatures.R;
import com.clear.androidfeatures.jobscheduler.services.JobInfoBuilderTestsJobService;
import com.clear.androidfeatures.jobscheduler.services.JobServiceBase;

public class JobInfoBuilderActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobinfo_builder);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jobinfo_builder: {
                JobInfoBuilderTestsJobService.scheduleJobInfoBuilderTests(getBaseContext());
                break;
            }

            case R.id.cancel_job: {
                JobInfoBuilderTestsJobService.cancelJob(getBaseContext(), JobServiceBase.Jobs.JOBINFO_BUILDER.getJobId());
                break;
            }
        }
    }
}
