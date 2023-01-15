package com.example.chatgpt

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


class SnackBarUtils {
    companion object {
        fun showCustomSnackBar(
            view: View,
            message: String,
            isError: Boolean? = null,
            requiredColor: String? = null
        ) {
            var color: Int? = null
            val snack: Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            val viewInSnk = snack.view
            val sbTextView =
                viewInSnk.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            if (isError != null)
                if (isError == true) {
                    if (requiredColor != null) {
                        color = Color.parseColor(requiredColor)
                    } else {
                        color = Color.RED
                    }
                    sbTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0)
                } else {
                    color = Color.parseColor("#53C107")
                    sbTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_tick_mark,
                        0,
                        0,
                        0
                    )
                }
            else
                Color.BLACK

            snack.setActionTextColor(Color.WHITE) //text color
            color?.let { snack.setBackgroundTint(it) } // background color
            sbTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER // text alignment
            sbTextView.textSize = 18F // text size
            sbTextView.setTypeface(sbTextView.typeface, Typeface.NORMAL) // text style
            val params: ViewGroup.MarginLayoutParams =
                viewInSnk.layoutParams as (ViewGroup.MarginLayoutParams)
            params.setMargins(0, 0, 0, 0)
            snack.view.layoutParams = params // set width to match parent
            viewInSnk.setBackgroundResource(R.drawable.snack_bar)
            snack.show()
        }
    }
}