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
import com.jmstudios.redmoon.util.filterIsOn

class ExtensionContentProvider : ContentProvider() {

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        return when (method) {
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
                    context.startActivity(intent)
                }
                null
            }
            GET_STATUS -> {
                bundleOf(
                    Pair(
                        WORK_STATUS,
                        if (filterIsOn) {
                            AppExtensionState.START
                        } else {
                            AppExtensionState.STOP
                        }
                            .id
                    )
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

    companion object {
        private const val PREFERENCE_AUTHORITY =
            "com.fulldive.extension.eyefilter.FulldiveContentProvider"
        const val BASE_URL = "content://$PREFERENCE_AUTHORITY"
        const val WORK_STATUS = "work_status"
        const val GET_STATUS = "GET_STATUS"
    }
}

fun getContentUri(value: String): Uri {
    return Uri
        .parse(ExtensionContentProvider.BASE_URL)
        .buildUpon().appendPath(ExtensionContentProvider.WORK_STATUS)
        .appendPath(value)
        .build()
}