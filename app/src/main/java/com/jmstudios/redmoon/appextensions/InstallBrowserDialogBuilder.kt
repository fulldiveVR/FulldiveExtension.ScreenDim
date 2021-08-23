package com.jmstudios.redmoon.appextensions

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.jmstudios.redmoon.R

object InstallBrowserDialogBuilder {

    private var dialog: AlertDialog? = null

    fun show(context: Context, onPositiveClicked: () -> Unit) {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.install_browser_dialog_layout, null)
        AlertDialog
            .Builder(context)
            .setView(view)
            .setPositiveButton(R.string.install_submit) { _, _ ->
                onPositiveClicked.invoke()
            }
            .setNegativeButton(R.string.rate_cancel) { _, _ -> }
            .create().apply {
                setButtonColor(
                    AlertDialog.BUTTON_POSITIVE,
                    context.getColor(R.color.textColorAccent)
                )
                setButtonColor(
                    AlertDialog.BUTTON_NEGATIVE,
                    context.getColor(R.color.textColorSecondary)
                )
            }
            .show()
    }

    private fun setButtonColor(buttonId: Int, color: Int) {
        dialog?.getButton(buttonId)?.setTextColor(color)
    }
}