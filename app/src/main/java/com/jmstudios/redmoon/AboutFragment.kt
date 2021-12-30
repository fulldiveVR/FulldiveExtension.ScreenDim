/*
 * Copyright (C) 2021 FullDive
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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