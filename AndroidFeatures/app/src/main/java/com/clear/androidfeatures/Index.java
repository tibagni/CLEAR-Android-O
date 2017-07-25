package com.clear.androidfeatures;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tbagni on 7/25/17.
 */

public class Index {
    private static final String ACTION_FEATURE_ACTIVITY = "com.clear.ACTION_FEATURE_ACTIVITY";

    private final Context context;
    private List<Entry> entries;

    public Index(Context context) {
        this.context = context;
        this.entries = new ArrayList<>();
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void initialize() {
        PackageManager pm = context.getPackageManager();
        String myPackageName = context.getPackageName();
        List<ResolveInfo> activities = listActivities(pm, myPackageName);

        for (ResolveInfo activity : activities) {
            ActivityInfo activityInfo = activity.activityInfo;
            ComponentName activityComponent = new ComponentName(myPackageName,
                    activityInfo.name);

            Intent startIntent = new Intent(ACTION_FEATURE_ACTIVITY);
            startIntent.setComponent(activityComponent);

            entries.add(new Entry(activityInfo.loadLabel(pm), startIntent));
        }
    }

    private List<ResolveInfo> listActivities(PackageManager pm, String packageName) {
        Intent activitiesIntent = new Intent(ACTION_FEATURE_ACTIVITY);
        // Look only for activities of the given packageName
        activitiesIntent.setPackage(packageName);
        return pm.queryIntentActivities(activitiesIntent, PackageManager.MATCH_ALL);
    }

    public static class Entry {
        public final CharSequence title;
        public final Intent startIntent;

        private Entry(CharSequence title, Intent startIntent) {
            this.title = title;
            this.startIntent = startIntent;
        }
    }
}
