package com.example.chatgpt


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat

object ExtensionFunctionUtils {

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        var drawable: Drawable? =
            AppCompatResources.getDrawable(this.context, R.drawable.ic_send)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, Color.parseColor("#045E86"))
        var stopAfterTextChange = false

        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.toString().trim().isBlank()) {
                    setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        0,
                        0
                    )
                    stopAfterTextChange = false
                }
                if (!stopAfterTextChange)
                    if (checkManual(editable.toString())) {
                        stopAfterTextChange = true
                        setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            drawable,
                            null
                        )
                    } else {
                        afterTextChanged.invoke(editable.toString())
                        setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )
                    }
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        /**task on touching send icon*/
        this.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (compoundDrawables[DRAWABLE_RIGHT] != null && event.rawX >= right - compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                ) {
                    afterTextChanged.invoke(this.toString().trim())
                    // stopAfterTextChange = false
                    return@OnTouchListener true
                }
            }
            false
        })

    }

    fun checkManual(barcode: String): Boolean {
        var isManual = false
        if (barcode.length == 1) {
            isManual = true
        }
        return isManual
    }

    fun EditText.afterTextChangedSimple(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}