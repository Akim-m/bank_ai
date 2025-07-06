package com.example.bank_ai.ui.sms

import java.text.SimpleDateFormat
import java.util.*

data class SmsMessage(
    val id: String,
    val address: String,
    val body: String,
    val date: Long,
    val type: Int // 1 for received, 2 for sent
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(date))
    }
    
    fun getSenderDisplayName(): String {
        return if (address.isNotEmpty()) {
            // Try to format as phone number if it's numeric
            if (address.matches(Regex("^\\+?[0-9]+$"))) {
                formatPhoneNumber(address)
            } else {
                address
            }
        } else {
            "Unknown"
        }
    }
    
    fun isBankMessage(): Boolean {
        val senderName = getSenderDisplayName().uppercase()
        val addressUpper = address.uppercase()
        
        // Check for common bank-related keywords
        val bankKeywords = listOf("BANK", "BNK", "CREDIT", "DEBIT", "TRANSACTION", "ACCOUNT", "BALANCE")
        
        return bankKeywords.any { keyword ->
            senderName.contains(keyword) || addressUpper.contains(keyword)
        }
    }
    
    private fun formatPhoneNumber(phoneNumber: String): String {
        return when {
            phoneNumber.startsWith("+1") && phoneNumber.length == 12 -> {
                // US format: +1 (XXX) XXX-XXXX
                "(${phoneNumber.substring(2, 5)}) ${phoneNumber.substring(5, 8)}-${phoneNumber.substring(8)}"
            }
            phoneNumber.length == 10 -> {
                // US format without country code: (XXX) XXX-XXXX
                "(${phoneNumber.substring(0, 3)}) ${phoneNumber.substring(3, 6)}-${phoneNumber.substring(6)}"
            }
            else -> phoneNumber
        }
    }
} 