package com.example.bank_ai.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bank_ai.service.LiteRTService
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val liteRTService = LiteRTService()
    
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _modelLoaded = MutableLiveData<Boolean>()
    val modelLoaded: LiveData<Boolean> = _modelLoaded

    init {
        // Initialize with welcome message
        val welcomeMessage = ChatMessage(
            content = "Hello! I'm your AI banking assistant. How can I help you today?",
            isUser = false
        )
        _messages.value = listOf(welcomeMessage)
        
        // Load model (you can specify the path to your LiteRT model here)
        loadModel()
    }
    
    private fun loadModel() {
        viewModelScope.launch {
            _isLoading.value = true
            // Replace with actual model path when you have a LiteRT model
            val success = liteRTService.loadModel("path/to/your/model")
            _modelLoaded.value = success
            _isLoading.value = false
        }
    }
    
    fun sendMessage(content: String) {
        if (content.trim().isEmpty()) return
        
        val userMessage = ChatMessage(
            content = content.trim(),
            isUser = true
        )
        
        // Add user message to chat
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(userMessage)
        _messages.value = currentMessages
        
        // Generate AI response
        generateAIResponse(content.trim())
    }
    
    private fun generateAIResponse(userMessage: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val response = liteRTService.generateResponse(userMessage)
                val aiMessage = ChatMessage(
                    content = response,
                    isUser = false
                )
                
                val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                currentMessages.add(aiMessage)
                _messages.value = currentMessages
                
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "Sorry, I encountered an error. Please try again.",
                    isUser = false
                )
                val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                currentMessages.add(errorMessage)
                _messages.value = currentMessages
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearChat() {
        val welcomeMessage = ChatMessage(
            content = "Hello! I'm your AI banking assistant. How can I help you today?",
            isUser = false
        )
        _messages.value = listOf(welcomeMessage)
    }
}