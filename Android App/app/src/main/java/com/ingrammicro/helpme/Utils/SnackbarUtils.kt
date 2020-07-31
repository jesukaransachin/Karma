package com.ingrammicro.helpme.utils

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ingrammicro.helpme.R


fun shortSnackbar(view: View, resId: Int): Snackbar {
    return yellowSnackbar(view, resId, Snackbar.LENGTH_SHORT)
}


fun shortSnackbar(view: View, message: String): Snackbar {
    return yellowSnackbar(
        view,
        message,
        Snackbar.LENGTH_SHORT
    ).setDuration(7000)
}


fun longSnackbar(view: View, resId: Int): Snackbar {
    return yellowSnackbar(view, resId, Snackbar.LENGTH_LONG).setDuration(7000)
}


fun longSnackbar(view: View, message: String): Snackbar {
    return yellowSnackbar(view, message, Snackbar.LENGTH_LONG).setDuration(7000)
}


fun yellowSnackbar(view: View, resId: Int, duration: Int): Snackbar {
    return createSnackbar(
        Snackbar.make(view, resId, duration),
        R.color.black,
        R.color.primaryDarkColor,
        R.color.color_snackbar_bg
    )
}

fun yellowSnackbar(view: View, message: String, duration: Int): Snackbar {
    return createSnackbar(
        Snackbar.make(view, message, duration),
        R.color.black,
        R.color.primaryDarkColor,
        R.color.color_snackbar_bg
    )
}

private fun createSnackbar(
    snackBar: Snackbar,
    textColor: Int = R.color.white,
    actionColor: Int = R.color.white,
    background: Int = R.color.primaryDarkColor
): Snackbar {
    val sbView = snackBar.view
    sbView.setBackgroundResource(background)
    val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.setTextColor(ContextCompat.getColor(snackBar.context, textColor))
//    val typeface = ResourcesCompat.getFont(snackBar.context, R.font.montserrat_regular)
//    textView.setTypeface(typeface)
    textView.maxLines = 4

    snackBar.setActionTextColor(ContextCompat.getColor(snackBar.context, actionColor))
    return snackBar
}