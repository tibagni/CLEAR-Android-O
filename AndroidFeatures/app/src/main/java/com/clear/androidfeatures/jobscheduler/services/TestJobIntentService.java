package com.clear.androidfeatures.jobscheduler.services;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by josegsp on 8/14/17.
 */

public class TestJobIntentService extends JobIntentService {
    private static final String TAG = TestJobIntentService.class.getSimpleName();

    private static final int JOB_INTENT_SERVICE_ID = 1000;
    private static final int TOAST_MSG = 100;

    private static final String EXTRA_QUEUE_ORDER = "queue_order";

    private static int sQueueOrder;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueue(Context context) {
        Log.d(TAG, "Enqueue work.");

        for (int i=0; i<3; i++) {
            sQueueOrder++;
            Intent intent = new Intent();
            intent.putExtra(EXTRA_QUEUE_ORDER, sQueueOrder);

            enqueueWork(context, TestJobIntentService.class, JOB_INTENT_SERVICE_ID, intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Create JobIntentService.");

        sQueueOrder = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy JobIntentService.");

        sQueueOrder = 0;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "Handle work");

        String toastMsg = (new StringBuilder())
                .append("Queue order: ")
                .append(getQueueOrder(intent))
                .toString();

        mHandler.sendMessageDelayed(mHandler.obtainMessage(TOAST_MSG, toastMsg), 1000);

        sQueueOrder--;
    }

    private int getQueueOrder(Intent i) {
        if (i.hasExtra(EXTRA_QUEUE_ORDER)) {
            return i.getIntExtra(EXTRA_QUEUE_ORDER, -1);
        }

        return -1;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST_MSG:
                    Toast.makeText(getBaseContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
