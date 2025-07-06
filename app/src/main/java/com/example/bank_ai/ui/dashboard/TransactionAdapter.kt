package com.example.bank_ai.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_ai.R

class TransactionAdapter(
    private var transactions: List<Transaction> = emptyList()
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val transactionTitle: TextView = view.findViewById(R.id.transactionTitle)
        val transactionDate: TextView = view.findViewById(R.id.transactionDate)
        val transactionAmount: TextView = view.findViewById(R.id.transactionAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.transactionTitle.text = transaction.title
        holder.transactionDate.text = transaction.getRelativeDate()
        holder.transactionAmount.text = transaction.getFormattedAmount()
        
        val colorRes = if (transaction.type == TransactionType.DEBIT) {
            R.color.balance_negative
        } else {
            R.color.balance_positive
        }
        holder.transactionAmount.setTextColor(holder.itemView.context.getColor(colorRes))
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
} 