package com.example.bank_ai.ui.home

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bank_ai.ui.home.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var llmInference: Any? = null
    private var modelLoaded = false

    init {
        val welcomeMessage = ChatMessage(
            content = "Hello! I'm your AI assistant. How can I help you today?",
            isUser = false
        )
        _messages.value = listOf(welcomeMessage)
        loadModel()
    }

    private fun loadModel() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val modelFile = findModelFile()
                if (modelFile != null) {
                    try {
                        val clazzOptions = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInferenceOptions")
                        val clazzInference = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInference")
                        val builderMethod = clazzOptions.getMethod("builder")
                        val builder = builderMethod.invoke(null)
                        val setModelPath = builder.javaClass.getMethod("setModelPath", String::class.java)
                        setModelPath.invoke(builder, modelFile.absolutePath)
                        val setMaxTopK = builder.javaClass.getMethod("setMaxTopK", Int::class.javaPrimitiveType)
                        setMaxTopK.invoke(builder, 64)
                        val build = builder.javaClass.getMethod("build")
                        val options = build.invoke(builder)
                        val createFromOptions = clazzInference.getMethod("createFromOptions", Application::class.java, clazzOptions)
                        llmInference = createFromOptions.invoke(null, getApplication<Application>(), options)
                        modelLoaded = true
                    } catch (e: ClassNotFoundException) {
                        postError("LLM Inference API not found. Please check your dependencies.")
                    } catch (e: Exception) {
                        postError("Failed to initialize LLM: ${e.message}")
                    }
                } else {
                    postError("No LLM model found in Downloads/models.")
                }
            } catch (e: Exception) {
                postError("Failed to load LLM model: ${e.message}")
            }
        }
    }

    private fun findModelFile(): File? {
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val modelsDir = File(downloads, "models")
        if (!modelsDir.exists() || !modelsDir.isDirectory) return null
        val modelFile = File(modelsDir, "gemma-1.1-2b-it-cpu-int4.bin")
        return if (modelFile.exists()) modelFile else null
    }

    fun sendMessage(content: String) {
        if (content.trim().isEmpty()) return
        val userMessage = ChatMessage(content = content.trim(), isUser = true)
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(userMessage)
        _messages.value = currentMessages
        generateLLMResponse(content.trim())
    }

    private fun generateLLMResponse(userMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                if (!modelLoaded || llmInference == null) {
                    postError("Model not loaded. Please ensure the model is in Downloads/models and dependencies are correct.")
                    _isLoading.postValue(false)
                    return@launch
                }
                try {
                    val generateResponse = llmInference!!::class.java.getMethod("generateResponse", String::class.java)
                    val response = generateResponse.invoke(llmInference, userMessage) as? String
                    val aiMessage = ChatMessage(content = response ?: "(No response)", isUser = false)
                    val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                    currentMessages.add(aiMessage)
                    _messages.postValue(currentMessages)
                } catch (e: Exception) {
                    postError("LLM inference error: ${e.message}")
                }
            } catch (e: Exception) {
                postError("Error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun postError(msg: String) {
        val errorMessage = ChatMessage(content = msg, isUser = false)
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(errorMessage)
        _messages.postValue(currentMessages)
    }

    fun clearChat() {
        val welcomeMessage = ChatMessage(
            content = "Hello! I'm your AI assistant. How can I help you today?",
            isUser = false
        )
        _messages.value = listOf(welcomeMessage)
    }
}