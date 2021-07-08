/*
 * Copyright (c) 2017  Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.jmstudios.redmoon

import android.app.Application
import com.jmstudios.redmoon.util.Logger

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

import com.jmstudios.redmoon.model.Config
import com.jmstudios.redmoon.model.Profile
import com.jmstudios.redmoon.model.ProfilesModel
import com.jmstudios.redmoon.schedule.ScheduleReceiver

import org.json.JSONObject

class RedMoonApplication: Application() {

    override fun onCreate() {
        Log.i("onCreate -- Initializing appContext")
        app = this
        super.onCreate()
        upgradeFrom(Config.fromVersionCode)
        val theme = when (Config.darkThemeFlag) {
            true -> AppCompatDelegate.MODE_NIGHT_YES
            false -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(theme)
        //EventBus.builder().addIndex(eventBusIndex()).installDefaultEventBus()
    }

    private tailrec fun upgradeFrom(version: Int): Unit = when (version) {
        BuildConfig.VERSION_CODE -> {
            Config.fromVersionCode = version
        } -1 -> { // fresh install
            ProfilesModel.restoreDefaultProfiles()
            upgradeFrom(BuildConfig.VERSION_CODE)
        } else -> {
            Log.e("Didn't catch upgrades from version $version")
            upgradeFrom(version+1)
        }
    }

    companion object : Logger() {
        lateinit var app: RedMoonApplication
            private set
    }
}
