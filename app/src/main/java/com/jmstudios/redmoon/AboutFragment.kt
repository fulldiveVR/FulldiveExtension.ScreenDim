package com.jmstudios.redmoon

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

import com.jmstudios.redmoon.util.*

class AboutFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.about, rootKey)
        pref(R.string.pref_key_version)?.let { versionPref ->
            versionPref.summary = BuildConfig.VERSION_NAME
            versionPref.setOnPreferenceClickListener { _ ->
                activity?.let { showChangelog(it) }
                true
            }
        }
    }

    companion object : Logger()
}