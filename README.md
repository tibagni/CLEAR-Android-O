# CLEAR-Android-O
Examples of new Android Features

# Adding a new Activity to the Index
1. Create a new Activity and declare it on AndroidManifest.xml
2. Add the __com.clear.ACTION_FEATURE_ACTIVITY__ action to it.

Example:

        <activity android:name=".jobscheduler.JobSchedulerActivity"
            android:label="@string/job_scheduler_title">
            <intent-filter>
                <action android:name="com.clear.ACTION_FEATURE_ACTIVITY" />
            </intent-filter>
        </activity>
