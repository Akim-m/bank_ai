package com.example.bank_ai.ui.dashboard

import java.text.SimpleDateFormat
import java.util.*

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.DEBIT,
    val category: String = "General"
) {
    fun getFormattedAmount(): String {
        val prefix = if (type == TransactionType.DEBIT) "-" else "+"
        return "$prefix$${String.format("%.2f", amount)}"
    }
    
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        return sdf.format(Date(date))
    }
    
    fun getRelativeDate(): String {
        val now = System.currentTimeMillis()
        val diff = now - date
        val days = diff / (24 * 60 * 60 * 1000)
        
        return when {
            days == 0L -> "Today"
            days == 1L -> "Yesterday"
            days < 7 -> "${days} days ago"
            else -> getFormattedDate()
        }
    }
}

enum class TransactionType {
    DEBIT, CREDIT
} 