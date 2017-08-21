package com.clear.androidfeatures.jobscheduler.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.clear.androidfeatures.R;
import com.clear.androidfeatures.jobscheduler.services.TestJobIntentService;

/**
 * Created by josegsp on 8/14/17.
 */

public class JobIntentServiceActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobintentservice);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enqueue_jobintentservice: {
                TestJobIntentService.enqueue(getBaseContext());
                break;
            }

            case R.id.enqueue_jobintentservice_delayed: {
                TestJobIntentService.enqueueDelayed(getBaseContext());
                break;
            }
        }
    }
}
