package com.example.bank_ai.ui.sms

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SmsViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableLiveData<List<SmsMessage>>()
    val messages: LiveData<List<SmsMessage>> = _messages

    private val _filteredMessages = MutableLiveData<List<SmsMessage>>()
    val filteredMessages: LiveData<List<SmsMessage>> = _filteredMessages

    private val _hasPermission = MutableLiveData<Boolean>()
    val hasPermission: LiveData<Boolean> = _hasPermission

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showBankOnly = MutableLiveData<Boolean>(true) // Default to showing bank messages only
    val showBankOnly: LiveData<Boolean> = _showBankOnly

    fun loadSmsMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            val smsMessages = withContext(Dispatchers.IO) {
                readSmsMessages()
            }
            _messages.value = smsMessages
            applyFilter()
            _isLoading.value = false
        }
    }

    fun setPermissionStatus(hasPermission: Boolean) {
        _hasPermission.value = hasPermission
        if (hasPermission) {
            loadSmsMessages()
        }
    }

    fun toggleBankFilter(showBankOnly: Boolean) {
        _showBankOnly.value = showBankOnly
        applyFilter()
    }

    private fun applyFilter() {
        val allMessages = _messages.value ?: emptyList()
        val showBankOnly = _showBankOnly.value ?: true
        
        val filteredList = if (showBankOnly) {
            allMessages.filter { message ->
                message.isBankMessage()
            }
        } else {
            allMessages
        }
        
        _filteredMessages.value = filteredList
    }

    private fun readSmsMessages(): List<SmsMessage> {
        val messages = mutableListOf<SmsMessage>()
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        
        val uri = Uri.parse("content://sms")
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.TYPE
        )
        
        val sortOrder = "${Telephony.Sms.DATE} DESC"
        
        contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val message = cursorToSmsMessage(cursor)
                messages.add(message)
            }
        }
        
        return messages
    }

    private fun cursorToSmsMessage(cursor: Cursor): SmsMessage {
        return SmsMessage(
            id = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms._ID)),
            address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)) ?: "",
            body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)) ?: "",
            date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)),
            type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
        )
    }
} 