package com.clear.androidfeatures.jobinfo

import android.app.job.JobParameters
import android.app.job.JobService
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.support.annotation.MainThread
import android.util.Log
import android.widget.Toast
import com.clear.androidfeatures.R

@MainThread
class JobInfoService : JobService() {
    companion object {
        private val TAG = JobInfoService::class.java.simpleName
    }

    private lateinit var mPlayer: MediaPlayer

    override fun onStartJob(params: JobParameters): Boolean {
        Log.v(TAG, "> onStartJob(params=$params)")

        // get custom message from job parameters' extras
        var extraMessage = params.extras.getString(JobInfoFragment.EXTRA_MESSAGE)?.trim()
        if (extraMessage.isNullOrEmpty()) {
            extraMessage = getString(R.string.job_info_message_when_empty)
        }
        Toast.makeText(this, getString(R.string.job_info_pref_toast, params.jobId, extraMessage), Toast.LENGTH_SHORT).show()

        // start playing the default notification tone in a separate thread and stop the job when it finishes
        val defaultNotificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mPlayer = MediaPlayer.create(this, defaultNotificationUri).apply {
            setOnCompletionListener { player ->
                Log.v(TAG, "> onCompletion(mp=$player)")

                player.release()
                jobFinished(params, false)

                Log.v(TAG, "< onCompletion(mp=$player)")
            }

            start()
        }

        // check if the job was triggered because of a ContentProvider change
        if (params.triggeredContentAuthorities != null) {
            params.triggeredContentUris?.forEach { uri -> Log.d(TAG, "Triggered URI: $uri") }

            val urisCount = (params.triggeredContentUris?.size ?: 0)
            Toast.makeText(this, resources.getQuantityString(R.plurals.job_info_toast_triggered_content, urisCount, urisCount), Toast.LENGTH_SHORT).show()
        }

        // the job should keep running because of MediaPlayer.start()
        val keepRunningJob = true

        Log.v(TAG, "< onStartJob(params=$params): $keepRunningJob")
        return keepRunningJob
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.v(TAG, "> onStopJob(params=$params)")

        // stop playing the tone and cleanup the MediaPlayer resources properly
        mPlayer.apply {
            stop()
            reset()
            release()
        }

        // if the job was stopped by the system (e.g. requirement statuses have changed),
        // let the system reschedule it
        // however, if the job was stopped because the user explicitly cancelled it,
        // it won't be rescheduled
        val shouldRescheduleJob = true

        Log.v(TAG, "< onStopJob(params=$params): $shouldRescheduleJob")
        return shouldRescheduleJob
    }
}