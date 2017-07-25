package com.clear.androidfeatures;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new IndexFragment(), IndexFragment.TAG)
                .commit();
    }

    public static class IndexFragment extends PreferenceFragment {
        private static final String TAG = "IndexFragment";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getContext());
            populateIndex(screen);
            setPreferenceScreen(screen);
        }

        private void populateIndex(PreferenceScreen prefScreen) {
            Index index = new Index(getContext());
            index.initialize();

            for (Index.Entry entry : index.getEntries()) {
                Preference pref = new Preference(getContext());
                pref.setTitle(entry.title);
                pref.setIntent(entry.startIntent);
                // We don't need to persist this preference to some xml file
                pref.setPersistent(false);

                prefScreen.addPreference(pref);
            }
        }
    }
}
