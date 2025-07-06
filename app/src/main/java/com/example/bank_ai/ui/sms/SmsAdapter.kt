package com.example.bank_ai.ui.sms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_ai.R

class SmsAdapter(
    private var messages: List<SmsMessage> = emptyList()
) : RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    class SmsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderTextView: TextView = view.findViewById(R.id.senderTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sms, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        val message = messages[position]
        holder.senderTextView.text = message.getSenderDisplayName()
        holder.dateTextView.text = message.getFormattedDate()
        holder.messageTextView.text = message.body
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<SmsMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }
} 