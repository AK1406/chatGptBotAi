package com.example.chatgpt


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgpt.databinding.ChatBinding
import com.example.chatgpt.databinding.ReceiverMsgItemBinding
import com.example.chatgpt.databinding.SenderMsgItemBinding
import kotlin.contracts.contract

class ChatAdapter(val context: Context) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {
    var messagesList = mutableListOf<ChatModel>()

    inner class MessageViewHolder(val binding: ChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

            binding.tvBotMessage.setOnLongClickListener {
                val layout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                }
                val drawable1 = ContextCompat.getDrawable(context, R.drawable.ic_delete)
                val imageView1 = ImageView(context).apply {
                    setImageDrawable(drawable1)
                }
                layout.addView(imageView1)
                val drawable2 = ContextCompat.getDrawable(context, R.drawable.ic_close)
                val imageView2 = ImageView(context).apply {
                    setImageDrawable(drawable2)
                }
                layout.addView(imageView2)
                val popupWindow = PopupWindow(
                    layout,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                popupWindow.showAsDropDown(binding.tvBotMessage)
                imageView1.setOnClickListener {
                    messagesList.removeAt(adapterPosition)
                    popupWindow.dismiss()
                    notifyItemRemoved(adapterPosition)

                }
                imageView2.setOnClickListener {
                    popupWindow.dismiss()
                }
                true

            }

            binding.tvMessage.setOnLongClickListener {
                val layout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                }
                val drawable1 = ContextCompat.getDrawable(context, R.drawable.ic_delete)
                val imageView1 = ImageView(context).apply {
                    setImageDrawable(drawable1)
                }
                layout.addView(imageView1)
                val drawable2 = ContextCompat.getDrawable(context, R.drawable.ic_close)
                val imageView2 = ImageView(context).apply {
                    setImageDrawable(drawable2)
                }
                layout.addView(imageView2)
                val popupWindow = PopupWindow(
                    layout,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val location = IntArray(2)
                binding.tvMessage.getLocationOnScreen(location)
                val x = location[0] + binding.tvMessage.width - popupWindow.width
                val y = location[1] + binding.tvMessage.height - popupWindow.height
                popupWindow.showAtLocation(binding.tvMessage, Gravity.START or Gravity.TOP, x, y)
                popupWindow.showAsDropDown(binding.tvMessage)
                imageView1.setOnClickListener {
                    messagesList.removeAt(adapterPosition)
                    popupWindow.dismiss()
                    notifyItemRemoved(adapterPosition)

                }
                imageView2.setOnClickListener {
                    popupWindow.dismiss()
                }
                true

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding =
            ChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]

        when (currentMessage.id) {
            "SEND_ID" -> {
                holder.binding.tvMessage.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                holder.binding.tvBotMessage.visibility = View.GONE
            }
            "RECEIVE_ID" -> {
                holder.binding.tvBotMessage.apply {
                    text = currentMessage.message
                    visibility = View.VISIBLE
                }
                holder.binding.tvMessage.visibility = View.GONE
            }
        }
    }


    fun insertMessage(message: ChatModel) {
        this.messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }

}