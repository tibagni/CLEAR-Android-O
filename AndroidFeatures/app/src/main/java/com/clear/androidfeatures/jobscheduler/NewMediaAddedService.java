package com.clear.androidfeatures.jobscheduler;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clear.androidfeatures.R;

/**
 * Created by tbagni on 8/1/17.
 *
 * This cannot be used with setPeriodic(long) or setPersisted(boolean). To continually monitor for
 * content changes, a new JobInfo observing the same URIs should be re-scheduled before the
 * execution of the JobService handling the most recent changes is finished.
 *
 * Following this pattern will ensure any content changes will not be lost:
 * while the job is running, the system will continue monitoring for content changes,
 * and propagate any it sees over to the next job scheduled.
 *
 * In order to keep observing for content changes across reboots, a new job must be re-scheduled
 * after BOOT COMPLETED
 *
 */
public class NewMediaAddedService extends JobService {
    private static final String TAG = "NewMediaAddedService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Uri[] triggeredContentUris = jobParameters.getTriggeredContentUris();
        StringBuilder urisStrings = new StringBuilder();

        for (Uri uri : triggeredContentUris) {
            urisStrings.append(uri.toString() + "\n");
        }


        Log.i(TAG, "triggeredContentUris: " + urisStrings);
        String notificationContent = triggeredContentUris.length > 1 ?
                (triggeredContentUris.length + " new media added") : urisStrings.toString();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("New media added!")
                .setContentText(notificationContent)
                .build());

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
