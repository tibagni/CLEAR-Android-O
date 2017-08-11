package com.clear.androidfeatures.jobinfo

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.clear.androidfeatures.R
import kotlinx.android.synthetic.main.fragment_job_info.*

class JobInfoFragment : Fragment() {
    companion object {
        val EXTRA_MESSAGE = "EXTRA_MESSAGE"

        private const val JOB_ID = 0
        private val TAG = JobInfoFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_info, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        button_schedule_job.setOnClickListener {
            try {
                scheduleJob()
            } catch (e: Exception) {
                Log.e(TAG, "failed to schedule job", e)
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        button_cancel_job.setOnClickListener { cancelJob() }
    }

    @UiThread
    private fun scheduleJob() {
        val jobInfo = buildJobInfo()
        Log.i(TAG, "scheduling job ${jobInfo.service}")
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = jobScheduler.schedule(jobInfo)

        Log.d(TAG, "job schedule result: $result")
        if (result == JobScheduler.RESULT_FAILURE) {
            throw IllegalStateException(getString(R.string.job_info_schedule_job_failed))
        }
    }

    @UiThread
    private fun cancelJob() {
        Log.i(TAG, "cancelling job")
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(JOB_ID)
    }

    @UiThread
    private fun buildJobInfo(): JobInfo {
        val extras = PersistableBundle().apply {
            putString(EXTRA_MESSAGE, edit_message.text.trim().toString())
        }

        val jobService = ComponentName(context, JobInfoService::class.java)
        val jobInfoBuilder = JobInfo.Builder(JOB_ID, jobService)
                .setExtras(extras)

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val requiresCharging = prefs.getBoolean("switch_charging", false)
        Log.d(TAG, "SET requires charging: $requiresCharging")
        jobInfoBuilder.setRequiresCharging(requiresCharging)

        val requiresIdle = prefs.getBoolean("switch_idle", false)
        Log.d(TAG, "SET requires idle: $requiresIdle")
        jobInfoBuilder.setRequiresDeviceIdle(requiresIdle)

        val shouldPersist = prefs.getBoolean("switch_persist", false)
        Log.d(TAG, "SET should persist: $shouldPersist")
        jobInfoBuilder.setPersisted(shouldPersist)

        val requiresNetworkType = prefs.getString("list_network_type", JobInfo.NETWORK_TYPE_NONE.toString()).toInt()
        Log.d(TAG, "SET requires network type: $requiresNetworkType")
        jobInfoBuilder.setRequiredNetworkType(requiresNetworkType)

        val backoffTime = prefs.getString("list_backoff_time", JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS.toString()).toLong()
        val backoffPolicy = prefs.getString("list_backoff_policy", JobInfo.BACKOFF_POLICY_EXPONENTIAL.toString()).toInt()
        if (backoffTime != JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS || backoffPolicy != JobInfo.BACKOFF_POLICY_EXPONENTIAL) {
            Log.d(TAG, "SET backoff time: $backoffTime")
            Log.d(TAG, "SET backoff policy: $backoffPolicy")
            jobInfoBuilder.setBackoffCriteria(backoffTime, backoffPolicy)
        }

        // setRequiresBatteryNotLow and setRequiresStorageNotLow only work on API 26+ (O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            jobInfoBuilder.apply {
                val batteryNotLow = prefs.getBoolean("switch_battery_not_low", false)
                Log.d(TAG, "SET battery not-low: $batteryNotLow")
                setRequiresBatteryNotLow(batteryNotLow)

                val storageNotLow = prefs.getBoolean("switch_storage_not_low", false)
                Log.d(TAG, "SET storage not-low: $storageNotLow")
                setRequiresStorageNotLow(storageNotLow)
            }
        }

        // setPeriodic(0) will use the minimum value (currently 15 min) as the period
        // so if we don't want to set the job as periodic, we cannot call setPeriodic
        val periodic = prefs.getString("list_periodic_time", "0").toLong()
        if (periodic > 0) {
            Log.d(TAG, "SET periodic time: $periodic")
            val flex = prefs.getString("list_flex_time", "0").toLong()
            if (flex > 0) {
                Log.d(TAG, "SET flex time: $flex")
                jobInfoBuilder.setPeriodic(periodic, flex)
            } else {
                jobInfoBuilder.setPeriodic(periodic)
            }
        }

        // setMinimumLatency(0) doesn't exactly start the job immediately (when possible)
        // if we want to do that, we just don't call that method
        val minimumLatency = prefs.getString("list_minimum_latency", "0").toLong()
        if (minimumLatency > 0) {
            Log.d(TAG, "SET minimum latency: $minimumLatency")
            jobInfoBuilder.setMinimumLatency(minimumLatency)
        }

        // setOverrideDeadline(0), as the usage implies, will start the job immediately
        // so if we don't want to set a deadline, we cannot call setOverrideDeadline
        val overrideDeadline = prefs.getString("list_override_deadline", "-1").toLong()
        if (overrideDeadline >= 0) {
            Log.d(TAG, "SET override deadline: $overrideDeadline")
            jobInfoBuilder.setOverrideDeadline(overrideDeadline)
        }

        val triggerContentUri = prefs.getString("edit_trigger_content_uri", "").trim()
        if (triggerContentUri.isNotEmpty()) {
            val triggerContentNotifyDescendants = prefs.getBoolean("switch_trigger_content_notify_descendants", false)
            val flag = if (triggerContentNotifyDescendants) JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS else 0
            Log.d(TAG, "SET trigger content URI: $triggerContentUri")
            Log.d(TAG, "SET trigger content notify descendants: $flag")
            jobInfoBuilder.addTriggerContentUri(JobInfo.TriggerContentUri(Uri.parse(triggerContentUri), flag))

            val triggerContentUpdateDelay = prefs.getString("list_trigger_content_update_delay", "10000").toLong()
            Log.d(TAG, "SET trigger content update delay: $triggerContentUpdateDelay")
            jobInfoBuilder.setTriggerContentUpdateDelay(triggerContentUpdateDelay)

            val triggerContentMaxDelay = prefs.getString("list_trigger_content_max_delay", "120000").toLong()
            Log.d(TAG, "SET trigger content max delay: $triggerContentMaxDelay")
            jobInfoBuilder.setTriggerContentMaxDelay(triggerContentMaxDelay)
        }

        return jobInfoBuilder.build()
    }
}