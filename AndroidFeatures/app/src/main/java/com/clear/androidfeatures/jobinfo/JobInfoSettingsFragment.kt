package com.clear.androidfeatures.jobinfo

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.support.annotation.MainThread
import android.view.View
import com.clear.androidfeatures.R

@MainThread
class JobInfoSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preference_job_info_settings)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // sync all ListPreference summaries with the corresponding selected entry text (during initialization and on change)
        (0 until preferenceScreen.preferenceCount)
                .map { i -> preferenceScreen.getPreference(i) }
                .filterIsInstance<ListPreference>()
                .forEach { pref ->
                    pref.apply {
                        setOnPreferenceChangeListener { _, newValue ->
                            val index = findIndexOfValue(newValue as String)
                            summary = entries[index]

                            return@setOnPreferenceChangeListener true
                        }

                        summary = entry
                    }
                }

        // sync EditTextPreference summary with text (during initialization and on change)
        (findPreference("edit_trigger_content_uri") as EditTextPreference).apply {
            setOnPreferenceChangeListener { _, newValue ->
                summary = newValue as String

                return@setOnPreferenceChangeListener true
            }

            summary = text
        }
    }
}