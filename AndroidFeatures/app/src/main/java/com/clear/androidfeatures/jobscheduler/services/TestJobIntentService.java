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
    private static final int MAX_JOBS = 10;

    private static final String EXTRA_QUEUE_ORDER = "queue_order";

    private static int sQueueOrder = 0;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueue(Context context) {
        Log.d(TAG, "Enqueue work.");

        for (int i=0; i<MAX_JOBS; i++) {
            sQueueOrder++;
            Intent intent = new Intent();
            intent.putExtra(EXTRA_QUEUE_ORDER, sQueueOrder);

            Log.d(TAG, "Enqueue work order: " + sQueueOrder);
            enqueueWork(context, TestJobIntentService.class, JOB_INTENT_SERVICE_ID, intent);
        }
    }

    /**
     * Convenience method for enqueuing delayed work in to this service.
     */
    public static void enqueueDelayed(final Context context) {
        Log.d(TAG, "Enqueue work delayed.");

        for (int i=0; i<MAX_JOBS; i++) {
            sQueueOrder++;
            final Intent intent = new Intent();
            intent.putExtra(EXTRA_QUEUE_ORDER, sQueueOrder);

            // Here we execute in another thread and wait for some time before calling enqueue
            // to simulate the case of JobIntentService executing in the order:
            //   onCreate -> onHandleWork -> onDestroy
            //
            // If we enqueue in sequence (just like it' done in enqueue() method), we have the
            // following execution order:
            //   onCreate -> onHandleWork1, onHandleWork2,..., onHandleWorkn, onDestroy
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000 * getQueueOrder(intent));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "Enqueue work delayed order: " + getQueueOrder(intent));
                    enqueueWork(context, TestJobIntentService.class, JOB_INTENT_SERVICE_ID, intent);
                }
            }).start();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Create JobIntentService.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy JobIntentService.");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String toastMsg = (new StringBuilder())
                .append("Queue order: ")
                .append(getQueueOrder(intent))
                .toString();

        Log.d(TAG, "Handle work. " + toastMsg);

        mHandler.sendMessageDelayed(mHandler.obtainMessage(TOAST_MSG, toastMsg), 100);

        sQueueOrder--;
    }

    private static int getQueueOrder(Intent i) {
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
