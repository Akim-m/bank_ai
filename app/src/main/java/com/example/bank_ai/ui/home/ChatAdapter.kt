package com.example.bank_ai.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_ai.R

class ChatAdapter(
    private var messages: List<ChatMessage> = emptyList()
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutRes = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_chat_user
        } else {
            R.layout.item_chat_bot
        }
        
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutRes, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.content
        holder.timeTextView.text = message.getFormattedTime()
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    fun addMessage(message: ChatMessage) {
        val newList = messages.toMutableList()
        newList.add(message)
        messages = newList
        notifyItemInserted(messages.size - 1)
    }
} 