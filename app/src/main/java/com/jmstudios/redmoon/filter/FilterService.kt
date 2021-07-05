/*
 * Copyright (c) 2016  Marien Raat <marienraat@riseup.net>
 * Copyright (c) 2017  Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *     Copyright (c) 2015 Chris Nguyen
 *
 *     Permission to use, copy, modify, and/or distribute this software
 *     for any purpose with or without fee is hereby granted, provided
 *     that the above copyright notice and this permission notice appear
 *     in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
 *     CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
 *     OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 *     NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 *     CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.jmstudios.redmoon.filter

import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jmstudios.redmoon.R
import com.jmstudios.redmoon.filter.overlay.Overlay
import com.jmstudios.redmoon.model.Config
import com.jmstudios.redmoon.model.Profile
import com.jmstudios.redmoon.securesuspend.CurrentAppMonitor
import com.jmstudios.redmoon.util.*
import org.greenrobot.eventbus.Subscribe
import java.util.*
import java.util.concurrent.Executors

class FilterService : JobService() {

    private lateinit var mFilter: Filter
    private lateinit var mAnimator: ValueAnimator
    private lateinit var mCurrentAppMonitor: CurrentAppMonitor
    private val executor = Executors.newSingleThreadScheduledExecutor()

    override fun onCreate() {
        super.onCreate()
        //hack to prevent service crash https://issuetracker.google.com/issues/76112072
        Log.d("onCreate $this")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showSimpleNotification()
            }
            mFilter = Overlay(this)
            mCurrentAppMonitor = CurrentAppMonitor(this, executor)
            mAnimator = ValueAnimator.ofObject(ProfileEvaluator(), mFilter.profile).apply {
                addUpdateListener { valueAnimator ->
                    mFilter.profile = valueAnimator.animatedValue as Profile
                    Log.i("addUpdateListener ${mFilter.profile}")
                }
            }
        } catch (e: Exception) {
            Log.e("error while starting foreground service", e)
        }
        Log.i("onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("onStartCommand($intent, $flags, $startId)")
        try {
            if (Permission.Overlay.isGranted) {
                val cmd = Command.getCommand(intent)
                val end = if (cmd.turnOn) activeProfile else mFilter.profile.off
                mAnimator.run {
                    Log.i("TestB Animating from ${mFilter.profile} to $end in $duration")
                    setObjectValues(mFilter.profile, end)
                    val fraction = if (isRunning) animatedFraction else 1f
                    duration = (cmd.time * fraction).toLong()
                    removeAllListeners()
                    addListener(CommandAnimatorListener(cmd, this@FilterService))
                    Log.i("Animating from ${mFilter.profile} to $end in $duration")
                    start()
                }
            } else {
                Log.i("Overlay permission denied.")
                EventBus.post(overlayPermissionDenied())
                onDestroy()
            }
        } catch (e: Exception) {
            Log.e("error while onStartCommand", e)
        }
        // Do not attempt to restart if the hosting process is killed by Android
        return Service.START_NOT_STICKY
    }

    fun start(isOn: Boolean) {
        if (!filterIsOn) {
            EventBus.register(this)
            filterIsOn = true
            mCurrentAppMonitor.monitoring = Config.secureSuspend
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(Intent(appContext, FilterService::class.java))
        } else {
            appContext.startService(Intent(appContext, FilterService::class.java))
        }
    }

    override fun onDestroy() {
        Log.i("onDestroy")
        try {
            EventBus.unregister(this)
            if (filterIsOn) {
                Log.w(" Service killed while filter was on!")
                filterIsOn = false
                mCurrentAppMonitor.monitoring = false
            }
            mFilter.onDestroy()
            executor.shutdownNow()
        } catch (e: Exception) {
            Log.w("Service killed while filter was on!, $e")
        }
        super.onDestroy()
    }

    @Subscribe
    fun onProfileUpdated(profile: Profile) {
        Log.i("onProfileUpdated $profile")
        mFilter.profile = profile
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(Intent(appContext, FilterService::class.java))
        } else {
            appContext.startService(Intent(appContext, FilterService::class.java))
        }
    }

    @Subscribe
    fun onSecureSuspendChanged(event: secureSuspendChanged) {
        mCurrentAppMonitor.monitoring = Config.secureSuspend
    }

    override fun onStartJob(params: JobParameters): Boolean = true
    override fun onStopJob(params: JobParameters): Boolean = false

    private fun showSimpleNotification() {
        try {
            startForeground(NOTIFICATION_ID, generateSimpleNotification())
        } catch (e: Exception) {
            Log.e("error while starting foreground service", e)
        }
    }

    private fun generateSimpleNotification(): Notification {
        val channel = NotificationChannel(
            NOTIFICATION_TAG,
            NOTIFICATION_TAG,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        (baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
        val builder = NotificationCompat.Builder(baseContext, NOTIFICATION_TAG)
            .setContentText("Full Blue Light Filter")
            .setSmallIcon(R.mipmap.ic_launcher_round)

        val notification = builder.build()
        notification.flags = Notification.FLAG_NO_CLEAR
        return notification
    }

    companion object : Logger() {
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_TAG = "eye_filter_fulldive_app"
    }
}
