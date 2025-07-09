package com.example.bank_ai.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bank_ai.ui.sms.SmsMessage
import java.util.regex.Pattern

class DashboardViewModel : ViewModel() {

    private val _checkingBalance = MutableLiveData<String>()
    val checkingBalance: LiveData<String> = _checkingBalance

    private val _savingsBalance = MutableLiveData<String>()
    val savingsBalance: LiveData<String> = _savingsBalance

    private val _creditBalance = MutableLiveData<String>()
    val creditBalance: LiveData<String> = _creditBalance

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    fun updateFromSms(smsMessages: List<SmsMessage>) {
        val transactions = smsMessages
            .filter { it.isBankMessage() }
            .mapNotNull { parseTransactionFromSms(it) }
            .sortedByDescending { it.date }
        _transactions.value = transactions
        updateBalances(transactions)
    }

    private fun updateBalances(transactions: List<Transaction>) {
        val checking = transactions.filter { it.category == "Checking" }
            .sumOf { if (it.type == TransactionType.CREDIT) it.amount else -it.amount }
        val savings = transactions.filter { it.category == "Savings" }
            .sumOf { if (it.type == TransactionType.CREDIT) it.amount else -it.amount }
        val credit = transactions.filter { it.category == "Credit" }
            .sumOf { if (it.type == TransactionType.CREDIT) it.amount else -it.amount }
        _checkingBalance.value = "$${String.format("%,.2f", checking)}"
        _savingsBalance.value = "$${String.format("%,.2f", savings)}"
        _creditBalance.value = "$${String.format("%,.2f", credit)}"
    }

    private fun parseTransactionFromSms(sms: SmsMessage): Transaction? {
        // Example patterns: "debited by $123.45", "credited with Rs. 5000", etc.
        val debitPattern = Pattern.compile("""(?i)(debited|spent|purchase).*?(\d+[.,]?\d*)""")
        val creditPattern = Pattern.compile("""(?i)(credited|received|deposit).*?(\d+[.,]?\d*)""")
        val amountRegex = Regex("""(\d+[.,]?\d*)""")
        val body = sms.body
        val lower = body.lowercase()
        val isDebit = debitPattern.matcher(body).find()
        val isCredit = creditPattern.matcher(body).find()
        val amountMatch = amountRegex.find(body.replace(",", ""))
        val amount = amountMatch?.value?.toDoubleOrNull() ?: return null
        val type = when {
            isDebit -> TransactionType.DEBIT
            isCredit -> TransactionType.CREDIT
            else -> TransactionType.DEBIT
        }
        val category = when {
            lower.contains("savings") -> "Savings"
            lower.contains("credit card") || lower.contains("credit") -> "Credit"
            else -> "Checking"
        }
        val title = sms.getSenderDisplayName()
        return Transaction(
            title = title,
            amount = amount,
            date = sms.date,
            type = type,
            category = category
        )
    }
}