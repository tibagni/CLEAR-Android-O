package com.clear.androidfeatures.jobintentservice

import android.content.ComponentName
import android.content.Intent
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import android.support.v4.app.JobIntentService
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

@MainThread
class JobIntentServiceService : JobIntentService() {
    companion object {
        private val TAG = JobIntentServiceService::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()

        Log.v(TAG, "onCreate()")
    }

    @WorkerThread
    override fun onHandleWork(intent: Intent) {
        Log.v(TAG, "> onHandleWork(intent=$intent)")

        repeat(100) { i ->
            if (isStopped) {
                Log.w(TAG, "job was stopped; aborting")
                return@repeat
            }

            Thread.sleep(16)
            broadcastUpdate(i + 1)
        }

        Log.v(TAG, "< onHandleWork(intent=$intent)")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.v(TAG, "onDestroy()")
    }

    private fun broadcastUpdate(progress: Int) {
        val broadcastIntent = Intent().apply {
            action = JobIntentServiceFragment.ACTION_PROGRESS_UPDATE
            component = ComponentName(baseContext, JobIntentServiceFragment.ProgressUpdateReceiver::class.java)
            putExtra(JobIntentServiceFragment.EXTRA_PROGRESS, progress)
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }
}