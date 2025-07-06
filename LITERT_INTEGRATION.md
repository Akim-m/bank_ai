# LiteRT Integration Guide

## Current Status
The chat interface is fully functional with a simulated AI service. The LiteRT dependency was removed due to JitPack access issues.

## How to Integrate LiteRT Later

### 1. Add LiteRT Dependency
When LiteRT becomes available, add to `app/build.gradle.kts`:
```kotlin
dependencies {
    // Add LiteRT dependency when available
    implementation("com.github.litert:litert-android:0.1.0")
}
```

### 2. Add JitPack Repository
Add to `settings.gradle.kts`:
```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

### 3. Update LiteRTService
Replace the simulation methods in `LiteRTService.kt`:

```kotlin
// Replace loadModel method
suspend fun loadLiteRTModel(modelPath: String): Boolean {
    try {
        // Initialize LiteRT with your model
        val model = LiteRT.loadModel(modelPath)
        return model != null
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

// Replace generateResponse method
suspend fun generateLiteRTResponse(prompt: String): String {
    try {
        // Use LiteRT to generate response
        val response = model.generate(prompt)
        return response
    } catch (e: Exception) {
        e.printStackTrace()
        return "Sorry, I encountered an error."
    }
}
```

### 4. Update HomeViewModel
In `HomeViewModel.kt`, replace the model loading call:
```kotlin
private fun loadModel() {
    viewModelScope.launch {
        _isLoading.value = true
        // Use actual LiteRT model path
        val success = liteRTService.loadLiteRTModel("path/to/your/litert/model")
        _modelLoaded.value = success
        _isLoading.value = false
    }
}
```

## Current Features
- ✅ Modern chat interface
- ✅ Message history
- ✅ Loading states
- ✅ Banking-specific responses
- ✅ Error handling
- ✅ Ready for LiteRT integration

## Testing the Chat
The chat interface works perfectly with simulated responses. You can test it by:
1. Opening the Home tab
2. Typing banking-related questions
3. Viewing AI responses

The AI responds to keywords like: balance, transfer, transaction, loan, credit, savings, investment, budget, security, fraud, etc. 