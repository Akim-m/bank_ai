package com.example.bank_ai.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bank_ai.ui.dashboard.Transaction
import com.example.bank_ai.ui.dashboard.TransactionType

class NotificationsViewModel : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    init {
        loadFakeTransactions()
    }

    private fun loadFakeTransactions() {
        _transactions.value = listOf(
            Transaction(title = "Coffee Shop", amount = 4.50, type = TransactionType.DEBIT, category = "Food"),
            Transaction(title = "Salary", amount = 2000.00, type = TransactionType.CREDIT, category = "Income"),
            Transaction(title = "Book Store", amount = 25.99, type = TransactionType.DEBIT, category = "Shopping"),
            Transaction(title = "Electricity Bill", amount = 60.00, type = TransactionType.DEBIT, category = "Bills"),
            Transaction(title = "Interest", amount = 1.25, type = TransactionType.CREDIT, category = "Interest"),
            Transaction(title = "Supermarket", amount = 80.00, type = TransactionType.DEBIT, category = "Groceries"),
            Transaction(title = "Transfer from Savings", amount = 300.00, type = TransactionType.CREDIT, category = "Transfer"),
            Transaction(title = "Online Subscription", amount = 12.99, type = TransactionType.DEBIT, category = "Entertainment")
        )
    }
}