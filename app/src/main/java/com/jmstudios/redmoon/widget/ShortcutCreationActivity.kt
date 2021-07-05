/*
 * Copyright (c) 2016 Marien Raat <marienraat@riseup.net>
 * Copyright (c) 2017  Stephen Michel <s@smichel.me>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.jmstudios.redmoon.widget

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.jmstudios.redmoon.R
import com.jmstudios.redmoon.MainActivity
import com.jmstudios.redmoon.util.Logger

class ShortcutCreationActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("Create ShortcutCreationActivity")
        super.onCreate(savedInstanceState)

        val shortcutIntent = Intent(this, MainActivity::class.java)
        shortcutIntent.putExtra(MainActivity.EXTRA_FROM_SHORTCUT_BOOL, true)
        shortcutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        // See: http://www.kind-kristiansen.no/2010/android-adding-desktop-shortcut-support-to-your-app/
        val iconResource = Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher_round)

        val intent = Intent()
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                resources.getString(R.string.shortcut_title_toggle))
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource)
        setResult(Activity.RESULT_OK, intent)

        finish()
    }
    companion object : Logger()
}
