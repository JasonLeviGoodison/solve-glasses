# Quick Reference Card

## 🎯 App Features at a Glance

| Feature            | How It Works                | Trigger                   |
| ------------------ | --------------------------- | ------------------------- |
| **Speech-to-Text** | Live transcription of audio | Automatic when app starts |
| **Voice Capture**  | Photo taken on keyword      | Say "solve"               |
| **Manual Capture** | Take photo on demand        | Tap CAPTURE button        |

---

## 🎨 UI Quick Reference

### Status Indicators

| Symbol              | Meaning                           |
| ------------------- | --------------------------------- |
| 🟢 ● LISTENING      | Speech recognition active         |
| ⚪ ○ NOT LISTENING  | Speech recognition paused         |
| 🔴 KEYWORD DETECTED | "Solve" detected, capturing photo |
| 🟦 Blue Card        | General status/info               |
| 🟩 Green Card       | Success message                   |
| 🟥 Red Card         | Error message                     |

### Button Actions

| Button              | Action                     |
| ------------------- | -------------------------- |
| **[START]** (Green) | Begin listening for speech |
| **[STOP]** (Red)    | Stop listening             |
| **[CAPTURE]**       | Take photo immediately     |

---

## 🔊 Voice Commands

| Say...     | Result                       |
| ---------- | ---------------------------- |
| "solve"    | Automatically captures photo |
| Any speech | Displays as live transcript  |

---

## 📂 File Locations

| Item                | Location                                           |
| ------------------- | -------------------------------------------------- |
| **Captured Photos** | `/Pictures/CheatingGlasses/`                       |
| **App Code**        | `/app/src/main/java/com/example/cheating_glasses/` |
| **Documentation**   | `/app/*.md`                                        |

---

## 🛠️ Developer Quick Commands

### Build & Run

```bash
./gradlew assembleDebug          # Build debug APK
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.cheating_glasses/.MainActivity
```

### Debugging

```bash
adb logcat | grep -i cheating    # View app logs
adb shell pm clear com.example.cheating_glasses  # Clear data
```

### Testing

```bash
adb pull /sdcard/Pictures/CheatingGlasses/ ./photos/  # Get photos
adb shell dumpsys batterystats | grep cheating  # Battery stats
```

---

## 🔧 Quick Modifications

### Change Keyword

```kotlin
// In VuzixViewModel.kt
keywordDetector = KeywordDetector("your_keyword")
```

### Change UI Color

```kotlin
// In MainActivity.kt
.background(Color.Black)  // Background
Text("...", color = Color.White)  // Text
```

### Change Photo Location

```kotlin
// In CameraManager.kt
put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/YourFolder")
```

### Change Language

```kotlin
// In SpeechRecognitionService.kt
putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")  // Spanish
```

---

## 🐛 Troubleshooting Quick Fixes

| Problem                 | Solution                                                      |
| ----------------------- | ------------------------------------------------------------- |
| **Not listening**       | Tap [STOP] then [START]                                       |
| **No transcript**       | Check microphone permission                                   |
| **Keyword not working** | Ensure "solve" is clearly spoken                              |
| **Photo not saving**    | Check storage permission                                      |
| **App crashed**         | Clear data: `adb shell pm clear com.example.cheating_glasses` |

---

## 📱 Permissions Required

| Permission               | Purpose                     |
| ------------------------ | --------------------------- |
| `RECORD_AUDIO`           | Speech recognition          |
| `CAMERA`                 | Photo capture               |
| `WRITE_EXTERNAL_STORAGE` | Save photos (Android 9-)    |
| `READ_MEDIA_IMAGES`      | Access photos (Android 13+) |

---

## 🏗️ Architecture Quick View

```
MainActivity (UI)
    ↓
VuzixViewModel (State)
    ↓
Services (SpeechRecognition, KeywordDetector, CameraManager)
    ↓
Android APIs (SpeechRecognizer, CameraX, MediaStore)
```

---

## 📊 Key Files

| File                          | Purpose              |
| ----------------------------- | -------------------- |
| `MainActivity.kt`             | UI & Compose screens |
| `VuzixViewModel.kt`           | State management     |
| `SpeechRecognitionService.kt` | Speech-to-text       |
| `KeywordDetector.kt`          | Keyword detection    |
| `CameraManager.kt`            | Camera & photos      |

---

## 🔗 Documentation Links

| For...              | Read...                                            |
| ------------------- | -------------------------------------------------- |
| **Using the app**   | [USER_GUIDE.md](USER_GUIDE.md)                     |
| **Developer setup** | [DEVELOPER_QUICKSTART.md](DEVELOPER_QUICKSTART.md) |
| **Installation**    | [SETUP_GUIDE.md](SETUP_GUIDE.md)                   |
| **Architecture**    | [ARCHITECTURE.md](ARCHITECTURE.md)                 |
| **Vuzix specifics** | [VUZIX_REFERENCE.md](VUZIX_REFERENCE.md)           |
| **Project status**  | [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)           |

---

## 📈 Performance Targets

| Metric            | Target   |
| ----------------- | -------- |
| Speech latency    | < 500ms  |
| Keyword detection | < 100ms  |
| Photo capture     | < 1s     |
| Battery life      | 2+ hours |
| Memory usage      | < 200MB  |

---

## 🎯 Quick Start Steps

### For Users

1. Launch app
2. Grant permissions
3. Start speaking
4. Say "solve" to capture

### For Developers

1. Open in Android Studio
2. Sync Gradle
3. Connect Vuzix device
4. Run (Shift + F10)

---

## 🚀 Deployment Checklist

- [ ] Test on Vuzix hardware
- [ ] Verify speech recognition
- [ ] Test photo capture
- [ ] Check battery usage
- [ ] Build release APK
- [ ] Sign APK
- [ ] Deploy

---

## 💡 Pro Tips

| Tip                 | Benefit               |
| ------------------- | --------------------- |
| Use dark UI         | Saves battery on OLED |
| Auto-restart speech | Continuous hands-free |
| StateFlow for state | Reactive, efficient   |
| CameraX API         | Modern, simple        |
| Compose UI          | Fast development      |

---

## 🔐 Security Notes

- Speech processed by Google API
- Photos stored locally only
- No cloud upload
- Permissions requested at runtime
- Tap STOP to pause listening

---

## 📞 Support Resources

| Resource       | Link/Location                                 |
| -------------- | --------------------------------------------- |
| Full docs      | `/app/DOCUMENTATION_INDEX.md`                 |
| Code           | `/app/src/main/java/...`                      |
| Vuzix dev      | https://vuzix.com/pages/developer             |
| Android camera | https://developer.android.com/training/camera |

---

## ✅ Feature Checklist

- ✅ Real-time speech-to-text
- ✅ Voice-triggered capture ("solve")
- ✅ Manual capture button
- ✅ Vuzix-optimized UI
- ✅ Permission handling
- ✅ Photo storage
- ✅ Error recovery
- ✅ Dark theme
- ✅ Status indicators
- ✅ High contrast text

---

## 🎨 Color Scheme

| Element      | Color  |
| ------------ | ------ |
| Background   | Black  |
| Primary text | White  |
| Labels       | Cyan   |
| Success      | Green  |
| Error        | Red    |
| Warning      | Yellow |
| Info         | Blue   |
| Inactive     | Gray   |

---

## 📐 Text Sizes (Vuzix Optimized)

| Element      | Size |
| ------------ | ---- |
| Title        | 16sp |
| Body         | 14sp |
| Transcript   | 18sp |
| Small/Helper | 10sp |

---

## 🔄 Data Flow Summary

1. **User speaks** → Microphone
2. **Android SpeechRecognizer** → Text
3. **SpeechRecognitionService** → StateFlow
4. **VuzixViewModel** → UI updates
5. **KeywordDetector** → Checks for "solve"
6. **CameraManager** → Captures photo
7. **MediaStore** → Saves to gallery

---

## 🌟 Key Technologies

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **State:** Kotlin Flow
- **Camera:** CameraX
- **Speech:** Android SpeechRecognizer
- **Async:** Coroutines

---

## 📏 Vuzix Blade 2 Specs

| Spec    | Value       |
| ------- | ----------- |
| Display | 640x360     |
| RAM     | 3-4GB       |
| Camera  | 12.8MP      |
| Battery | 3-4 hours   |
| OS      | Android 11+ |

---

**Print this page for quick reference!** 📄
