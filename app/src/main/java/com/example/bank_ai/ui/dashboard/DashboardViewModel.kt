package com.example.bank_ai.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _checkingBalance = MutableLiveData<String>()
    val checkingBalance: LiveData<String> = _checkingBalance

    private val _savingsBalance = MutableLiveData<String>()
    val savingsBalance: LiveData<String> = _savingsBalance

    private val _creditBalance = MutableLiveData<String>()
    val creditBalance: LiveData<String> = _creditBalance

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    init {
        loadAccountData()
        loadTransactions()
    }

    private fun loadAccountData() {
        _checkingBalance.value = "$12,345.67"
        _savingsBalance.value = "$45,678.90"
        _creditBalance.value = "$2,500.00"
    }

    private fun loadTransactions() {
        val sampleTransactions = listOf(
            Transaction(
                title = "Grocery Store",
                amount = 45.67,
                type = TransactionType.DEBIT,
                category = "Food"
            ),
            Transaction(
                title = "Salary Deposit",
                amount = 2500.00,
                type = TransactionType.CREDIT,
                category = "Income"
            ),
            Transaction(
                title = "Gas Station",
                amount = 35.50,
                type = TransactionType.DEBIT,
                category = "Transportation"
            ),
            Transaction(
                title = "Online Purchase",
                amount = 89.99,
                type = TransactionType.DEBIT,
                category = "Shopping"
            ),
            Transaction(
                title = "Interest Payment",
                amount = 12.45,
                type = TransactionType.CREDIT,
                category = "Interest"
            )
        )
        _transactions.value = sampleTransactions
    }

    fun refreshData() {
        loadAccountData()
        loadTransactions()
    }
}