package com.jmstudios.redmoon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.jmstudios.redmoon.appextensions.PopupManager
import com.jmstudios.redmoon.filter.Command
import com.jmstudios.redmoon.model.Config
import com.jmstudios.redmoon.model.ProfilesModel
import com.jmstudios.redmoon.settings.SettingsActivity
import com.jmstudios.redmoon.util.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.Subscribe
import java.io.IOException


class MainActivity : ThemedAppCompatActivity() {

    data class UI(val isOpen: Boolean) : EventBus.Event

    companion object : Logger() {
        const val EXTRA_FROM_SHORTCUT_BOOL =
            "com.jmstudios.redmoon.activity.MainActivity.EXTRA_FROM_SHORTCUT_BOOL"
    }

    override val fragment = FilterFragment()
    override val tag = "jmstudios.fragment.tag.FILTER"

    private val switchView: SwitchCompat get() = findViewById(R.id.switchView)

    override fun onCreate(savedInstanceState: Bundle?) {
        val fromShortcut = intent.getBooleanExtra(EXTRA_FROM_SHORTCUT_BOOL, false)
        Log.i("Got intent")
        if (fromShortcut) {
            toggleAndFinish()
        }

        super.onCreate(savedInstanceState)

        if (!Config.introShown) {
            switchView.visibility = View.GONE
            startActivity(intent(Intro::class))
        }
        showChangelogAuto(this)

        switchView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != filterIsOn) Command.toggle()
        }

        switchView.visibility = View.VISIBLE

        // Can't toggle from browser without permissions. Need to request it before stealth launch.
        Permission.Overlay.request(this)

        PopupManager.onAppStarted(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        EventBus.postSticky(UI(isOpen = true))
    }

    override fun onResume() {
        Log.i("onResume")
        super.onResume()
        setOnCheckChanged()
        EventBus.register(this)
    }

    override fun onPause() {
        EventBus.unregister(this)
        super.onPause()
    }

    override fun onStop() {
        EventBus.postSticky(UI(isOpen = false))
        super.onStop()
    }

    override fun onNewIntent(intent: Intent) {
        Log.i("onNewIntent")
        super.onNewIntent(intent)
        val fromShortcut = intent.getBooleanExtra(EXTRA_FROM_SHORTCUT_BOOL, false)
        if (fromShortcut) {
            toggleAndFinish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.menu_show_intro -> {
                startActivity(intent(Intro::class))
            }
            R.id.menu_about -> {
                startActivity(intent(AboutActivity::class))
            }
            R.id.menu_settings -> {
                startActivity(intent(SettingsActivity::class))
            }
            R.id.menu_restore_default_filters -> {
                ProfilesModel.restoreDefaultProfiles()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    @Subscribe
    fun onFilterIsOnChanged(event: filterIsOnChanged) {
        Log.i("FilterIsOnChanged")
        setOnCheckChanged()
    }

    @Subscribe
    fun onOverlayPermissionDenied(event: overlayPermissionDenied) {
        setOnCheckChanged(false)
        Permission.Overlay.request(this)
    }

    private fun setOnCheckChanged(on: Boolean = filterIsOn) {
        if (switchView.isChecked != on) switchView.isChecked = on
    }

    private fun toggleAndFinish() {
        Command.toggle(!filterIsOn)
        finish()
    }
}
