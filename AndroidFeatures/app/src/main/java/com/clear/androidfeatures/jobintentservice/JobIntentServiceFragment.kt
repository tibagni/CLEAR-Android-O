package com.clear.androidfeatures.jobintentservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.app.Fragment
import android.support.v4.app.JobIntentService
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clear.androidfeatures.R
import kotlinx.android.synthetic.main.fragment_job_intent_service.*

@MainThread
class JobIntentServiceFragment : Fragment() {
    companion object {
        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        val ACTION_PROGRESS_UPDATE = "${ProgressUpdateReceiver::class.java.name}.ACTION_PROGRESS_UPDATE"

        private const val JOB_ID = 10
        private val TAG = JobIntentServiceFragment::class.java.simpleName
    }

    private val mProgressUpdateReceiver = ProgressUpdateReceiver()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_intent_service, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        button_enqueue_job_intent.setOnClickListener { scheduleJob() }
    }

    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(context).registerReceiver(mProgressUpdateReceiver, IntentFilter(ACTION_PROGRESS_UPDATE))
    }

    override fun onStop() {
        super.onStop()

        LocalBroadcastManager.getInstance(context).unregisterReceiver(mProgressUpdateReceiver)
    }

    private fun scheduleJob() {
        JobIntentService.enqueueWork(context, JobIntentServiceService::class.java, JOB_ID, Intent())
    }

    inner class ProgressUpdateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PROGRESS_UPDATE -> progress_bar_job_intent.progress = intent.getIntExtra(EXTRA_PROGRESS, 0)
                else -> Log.wtf(TAG, "unexpected Intent action: ${intent.action}")
            }
        }
    }
}