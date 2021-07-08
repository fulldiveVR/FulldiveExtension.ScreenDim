package com.jmstudios.redmoon.appextensions

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import com.jmstudios.redmoon.MainActivity
import com.jmstudios.redmoon.filter.Command
import com.jmstudios.redmoon.util.Permission
import com.jmstudios.redmoon.util.filterIsOn
import java.util.*

class ExtensionContentProvider : ContentProvider() {

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        return when (method.toLowerCase(Locale.ENGLISH)) {
            WorkType.START.id -> {
                if (!filterIsOn) {
                    toggleFilter()
                }
                null
            }
            WorkType.STOP.id -> {
                if (filterIsOn) {
                    toggleFilter()
                }
                null
            }
            WorkType.OPEN.id -> {
                context?.let { context ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                null
            }
            WorkType.GetStatus.id -> {
                bundleOf(
                    KEY_WORK_STATUS to if (filterIsOn) {
                        AppExtensionState.START
                    } else {
                        AppExtensionState.STOP
                    }
                        .id
                )
            }
            WorkType.GetPermissionsRequired.id -> {
                bundleOf(
                    KEY_RESULT to getPermissionsRequired()
                )
            }
            else -> {
                super.call(method, arg, extras)
            }
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        context?.contentResolver?.notifyChange(uri, null)
        return uri
    }

    override fun getType(uri: Uri): String? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    private fun toggleFilter() {
        filterIsOn = !filterIsOn
        Command.toggle(filterIsOn)
    }

    private fun getPermissionsRequired(): Boolean {
        return !Permission.Overlay.granted
    }

    companion object {
        private const val PREFERENCE_AUTHORITY =
            "com.fulldive.extension.eyefilter.FulldiveContentProvider"
        const val BASE_URL = "content://$PREFERENCE_AUTHORITY"
        const val KEY_WORK_STATUS = "work_status"
        const val KEY_RESULT = "result"
    }
}

fun getContentUri(value: String): Uri {
    return Uri
        .parse(ExtensionContentProvider.BASE_URL)
        .buildUpon().appendPath(ExtensionContentProvider.KEY_WORK_STATUS)
        .appendPath(value)
        .build()
}