package com.example.chatgpt
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

open class ProgressDialog {
    companion object {
        fun getProgressDialog(context: Context,message: String): Dialog {
            val dialog = Dialog(context!!, R.style.RotatingProgressDialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            Objects.requireNonNull(dialog.window)!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setGravity(Gravity.CENTER)
            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.0f
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window
            dialog.window!!.attributes = lp
            val dialoglayout = View.inflate(
                context,
                R.layout.progress_bar, null
            )
            val tvMessage = dialoglayout.findViewById<TextView>(R.id.pbText)
            tvMessage.text = message
            dialog.setContentView(dialoglayout)
            return dialog
        }
    }

}