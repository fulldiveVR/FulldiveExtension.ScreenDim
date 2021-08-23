package com.jmstudios.redmoon.appextensions

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatRatingBar
import com.jmstudios.redmoon.R
import kotlin.math.roundToInt

object RateUsDialogBuilder {

    fun show(context: Context, onPositiveClicked: (Int) -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.rate_us_dialog_layout, null)
        val ratingBar = view.findViewById<AppCompatRatingBar>(R.id.ratingBar)
        var ratingValue = 0
        ratingBar.setOnRatingBarChangeListener { _, value, fromUser ->
            if (fromUser) {
                ratingValue = value.roundToInt()
            }
        }

        val builder = AlertDialog.Builder(context)
        builder
            .setView(view)
            .setTitle(R.string.rate_us_title)
            .setPositiveButton(R.string.rate_submit) { _, _ ->
                onPositiveClicked.invoke(ratingValue)
            }
            .setNegativeButton(R.string.rate_cancel) { _, _ -> }
            .create()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(context.getColor(R.color.textColorPrimary))
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(context.getColor(R.color.textColorSecondary))
            }
            .show()
    }
}