# Developer Quick Start Guide

## 🚀 Get Running in 5 Minutes

### Prerequisites

```bash
✓ Android Studio installed
✓ Vuzix Blade 2 device or emulator
✓ JDK 11+
```

### Quick Setup

```bash
# 1. Open project in Android Studio
# 2. Sync Gradle (should happen automatically)
# 3. Connect Vuzix device via USB
# 4. Run app (Shift + F10)
```

That's it! App should launch with full functionality.

## 📂 Project Structure at a Glance

```
app/src/main/java/com/example/cheating_glasses/
│
├── MainActivity.kt              ← UI entry point, Compose UI, permissions
├── VuzixViewModel.kt           ← State management, coordinates services
│
└── utils/
    ├── SpeechRecognitionService.kt  ← Handles speech-to-text
    ├── KeywordDetector.kt           ← Detects "solve" keyword
    └── CameraManager.kt             ← Manages camera capture
```

## 🔧 Key Components Explained

### 1. MainActivity.kt

```kotlin
// Main UI with Compose
VuzixApp(viewModel)
  ├── PermissionScreen     // If permissions not granted
  └── VuzixMainScreen      // Main interface
      ├── Transcript display
      ├── Status indicators
      └── Control buttons
```

**Key responsibilities:**

- Render Compose UI
- Handle permissions
- Observe ViewModel state
- Display real-time updates

### 2. VuzixViewModel.kt

```kotlin
class VuzixViewModel : ViewModel() {
    // State flows
    val transcript: StateFlow<String>
    val isListening: StateFlow<Boolean>
    val keywordDetected: StateFlow<Boolean>

    // Actions
    fun startListening()
    fun stopListening()
    fun capturePhoto()
}
```

**Key responsibilities:**

- Central state management
- Coordinates all services
- Exposes UI state via StateFlow
- Handles business logic

### 3. SpeechRecognitionService.kt

```kotlin
class SpeechRecognitionService(context: Context) {
    val transcriptFlow: StateFlow<String>
    val isListening: StateFlow<Boolean>

    fun startListening()
    fun stopListening()
    fun destroy()
}
```

**Key responsibilities:**

- Wraps Android SpeechRecognizer
- Provides continuous listening
- Emits transcript updates
- Auto-restarts on errors

### 4. KeywordDetector.kt

```kotlin
class KeywordDetector(keyword: String = "solve") {
    fun processText(text: String)
    var onKeywordDetected: ((String) -> Unit)?
}
```

**Key responsibilities:**

- Monitors text for keyword
- Case-insensitive matching
- Triggers callback on match
- Debounces duplicates

### 5. CameraManager.kt

```kotlin
class CameraManager(context: Context) {
    fun initializeCamera(lifecycleOwner, previewView?)
    fun capturePhoto(onSuccess, onError)
    val captureStatus: StateFlow<CaptureStatus>
}
```

**Key responsibilities:**

- CameraX integration
- Photo capture
- MediaStore saving
- Status reporting

## 🔄 Data Flow

### Speech → Transcript Flow

```
User speaks
    ↓
Android SpeechRecognizer
    ↓
SpeechRecognitionService.onPartialResults()
    ↓
_transcriptFlow.emit(text)
    ↓
VuzixViewModel.transcript
    ↓
UI recomposes
    ↓
Text displayed on screen
```

### Keyword → Photo Flow

```
Transcript updated
    ↓
KeywordDetector.processText(text)
    ↓
"solve" detected
    ↓
onKeywordDetected callback
    ↓
VuzixViewModel.capturePhoto()
    ↓
CameraManager.capturePhoto()
    ↓
Photo saved
    ↓
UI shows success message
```

## 🎨 Modifying the UI

### Changing Colors

```kotlin
// In MainActivity.kt

// Background
.background(Color.Black)  // Change to your color

// Text colors
Text("...", color = Color.White)  // Change to your color

// Button colors
ButtonDefaults.buttonColors(
    containerColor = Color.Green  // Change to your color
)
```

### Changing Text Size

```kotlin
Text(
    text = "...",
    fontSize = 18.sp  // Adjust size here
)
```

### Adding New UI Elements

```kotlin
// In VuzixMainScreen composable
Column {
    // Existing UI...

    // Add your new element here
    Text("My new element", color = Color.White)
}
```

## 🔊 Modifying Speech Recognition

### Change Keyword

```kotlin
// In VuzixViewModel.kt
keywordDetector = KeywordDetector("your_keyword")  // Change keyword here
```

### Add Multiple Keywords

```kotlin
// In KeywordDetector.kt
fun processText(text: String) {
    val keywords = listOf("solve", "capture", "photo")
    keywords.forEach { keyword ->
        if (text.contains(keyword, ignoreCase = true)) {
            onKeywordDetected?.invoke(text)
        }
    }
}
```

### Change Language

```kotlin
// In SpeechRecognitionService.kt
val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")  // Spanish
    // or "fr-FR" for French, etc.
}
```

### Adjust Silence Detection

```kotlin
// In SpeechRecognitionService.kt
intent.apply {
    // Increase for more patient listening
    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
             3000L)  // 3 seconds
}
```

## 📸 Modifying Camera Behavior

### Change Photo Quality

```kotlin
// In CameraManager.kt
ImageCapture.Builder()
    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)  // Faster
    // or CAPTURE_MODE_MAXIMIZE_QUALITY for better quality
    .build()
```

### Change Save Location

```kotlin
// In CameraManager.kt
contentValues.apply {
    put(MediaStore.Images.Media.RELATIVE_PATH,
        "Pictures/YourFolderName")  // Change folder name
}
```

### Add Photo Preview

```kotlin
// In CameraManager.kt - initializeCamera()
val preview = Preview.Builder().build()
previewView?.let {
    preview.setSurfaceProvider(it.surfaceProvider)  // Shows live preview
}
```

## 🧪 Testing Your Changes

### Quick Test Cycle

```bash
# 1. Make code changes
# 2. Build and run
./gradlew installDebug && adb shell am start -n com.example.cheating_glasses/.MainActivity

# 3. View logs
adb logcat | grep -E "VuzixApp|cheating"

# 4. Test feature
# 5. Repeat
```

### Debug Logging

```kotlin
// Add anywhere in your code
Log.d("VuzixApp", "Debug message: $value")

// View in logcat
adb logcat -s VuzixApp
```

### Test Speech Recognition

```kotlin
// Add to SpeechRecognitionService
override fun onResults(results: Bundle?) {
    Log.d("Speech", "Results: ${results?.getStringArrayList(...)}")
    // Your code...
}
```

### Test Keyword Detection

```kotlin
// Add to KeywordDetector
fun processText(text: String) {
    Log.d("Keyword", "Processing: $text")
    // Your code...
}
```

## 🐛 Common Issues & Quick Fixes

### Issue: Speech recognition doesn't start

```kotlin
// Check if permission granted
if (ContextCompat.checkSelfPermission(context,
    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
    Log.e("VuzixApp", "Missing RECORD_AUDIO permission")
}

// Check if service available
if (!SpeechRecognizer.isRecognitionAvailable(context)) {
    Log.e("VuzixApp", "Speech recognition not available")
}
```

### Issue: Photos not saving

```kotlin
// Check storage permission (for Android < 10)
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
    // Request WRITE_EXTERNAL_STORAGE
}

// Check available storage
val statFs = StatFs(Environment.getExternalStorageDirectory().path)
val availableBytes = statFs.availableBlocksLong * statFs.blockSizeLong
Log.d("Storage", "Available: ${availableBytes / 1024 / 1024} MB")
```

### Issue: UI not updating

```kotlin
// Ensure collecting StateFlow on Main thread
LaunchedEffect(Unit) {
    viewModel.transcript.collect { text ->
        Log.d("UI", "Transcript updated: $text")
        // Should trigger recomposition
    }
}
```

## 🚢 Building for Release

### 1. Configure Signing

```kotlin
// In build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/keystore.jks")
            storePassword = "your_store_password"
            keyAlias = "your_key_alias"
            keyPassword = "your_key_password"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 2. Build Release APK

```bash
./gradlew assembleRelease

# APK location:
# app/build/outputs/apk/release/app-release.apk
```

### 3. Test Release Build

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 📦 Adding New Dependencies

### Add to build.gradle.kts

```kotlin
dependencies {
    // Your new dependency
    implementation("com.example:library:1.0.0")
}
```

### Sync Gradle

```bash
./gradlew build --refresh-dependencies
```

## 🔐 ProGuard Rules (for Release)

### Keep Important Classes

```proguard
# In proguard-rules.pro

# Keep ViewModel
-keep class com.example.cheating_glasses.VuzixViewModel { *; }

# Keep utility classes
-keep class com.example.cheating_glasses.utils.** { *; }

# Keep CameraX
-keep class androidx.camera.** { *; }
```

## 🎯 Quick Feature Additions

### Add New Keyword Action

```kotlin
// 1. In KeywordDetector.kt
class KeywordDetector {
    private val keywords = mapOf(
        "solve" to KeywordAction.CAPTURE_PHOTO,
        "save" to KeywordAction.SAVE_TRANSCRIPT,
        "clear" to KeywordAction.CLEAR_TEXT
    )

    fun processText(text: String) {
        keywords.forEach { (keyword, action) ->
            if (text.contains(keyword, ignoreCase = true)) {
                onActionDetected?.invoke(action, text)
            }
        }
    }
}

enum class KeywordAction {
    CAPTURE_PHOTO, SAVE_TRANSCRIPT, CLEAR_TEXT
}
```

### Add Voice Feedback

```kotlin
// 1. Add dependency
implementation("androidx.core:core-ktx:1.12.0")

// 2. In VuzixViewModel.kt
private val tts = TextToSpeech(context) { status ->
    if (status == TextToSpeech.SUCCESS) {
        tts.language = Locale.US
    }
}

fun speakFeedback(message: String) {
    tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
}
```

### Add Transcript History

```kotlin
// In VuzixViewModel.kt
private val _transcriptHistory = MutableStateFlow<List<String>>(emptyList())
val transcriptHistory: StateFlow<List<String>> = _transcriptHistory.asStateFlow()

init {
    viewModelScope.launch {
        transcript.collect { text ->
            if (text.isNotEmpty()) {
                _transcriptHistory.value = _transcriptHistory.value + text
            }
        }
    }
}
```

## 📊 Performance Tips

### Optimize Recompositions

```kotlin
// Use remember and derivedStateOf
@Composable
fun VuzixMainScreen() {
    val transcript by viewModel.transcript.collectAsStateWithLifecycle()

    // Optimize expensive calculations
    val wordCount by remember {
        derivedStateOf { transcript.split(" ").size }
    }
}
```

### Reduce State Updates

```kotlin
// In SpeechRecognitionService.kt
override fun onPartialResults(partialResults: Bundle?) {
    val text = partialResults?.getStringArrayList(...)?.firstOrNull()

    // Only update if text actually changed
    if (text != null && text != _transcriptFlow.value) {
        _transcriptFlow.value = text
    }
}
```

### Memory Management

```kotlin
// In VuzixViewModel.kt
override fun onCleared() {
    super.onCleared()

    // Clean up all services
    speechRecognitionService?.destroy()
    cameraManager?.shutdown()

    // Clear heavy objects
    _transcriptHistory.value = emptyList()
}
```

## 🔗 Useful Commands

```bash
# View app logs
adb logcat | grep -i cheating

# Clear app data
adb shell pm clear com.example.cheating_glasses

# Force stop app
adb shell am force-stop com.example.cheating_glasses

# Start app
adb shell am start -n com.example.cheating_glasses/.MainActivity

# Check if app is running
adb shell ps | grep cheating

# Pull captured photos
adb pull /sdcard/Pictures/CheatingGlasses/ ./photos/

# Monitor performance
adb shell top | grep cheating_glasses

# Check battery usage
adb shell dumpsys batterystats | grep cheating_glasses
```

## 📚 Key Files Reference

| File                        | Purpose            | Key Methods                                    |
| --------------------------- | ------------------ | ---------------------------------------------- |
| MainActivity.kt             | UI & permissions   | onCreate(), VuzixApp()                         |
| VuzixViewModel.kt           | State coordination | initialize(), startListening(), capturePhoto() |
| SpeechRecognitionService.kt | Speech-to-text     | startListening(), onResults()                  |
| KeywordDetector.kt          | Keyword detection  | processText()                                  |
| CameraManager.kt            | Photo capture      | capturePhoto(), initializeCamera()             |

## 🎓 Next Steps

1. ✅ Run the app and test basic functionality
2. ✅ Read through MainActivity.kt to understand UI
3. ✅ Explore VuzixViewModel.kt for business logic
4. ✅ Check out utility classes in utils/
5. ✅ Make a small change and test it
6. ✅ Review ARCHITECTURE.md for deeper understanding
7. ✅ Build your own features!

## 💡 Pro Tips

1. **Use Android Studio's Logcat** effectively - filter by package name
2. **Hot reload works** with Compose - change UI without full rebuild
3. **StateFlow is key** - understand it for state management
4. **Breakpoints in callbacks** help debug speech recognition
5. **Test on real Vuzix** - emulator can't replicate AR display

---

Happy coding! 🚀 For detailed architecture, see ARCHITECTURE.md
