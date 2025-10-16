# Vuzix Blade 2 App - Setup Guide

## Prerequisites

### Development Environment

- Android Studio (latest stable version recommended)
- JDK 11 or higher
- Android SDK API 24-36
- Vuzix Blade 2 device or emulator

### Required Services

- Google Play Services installed on device
- Google Speech Services (for speech recognition)
- Internet connection (for cloud-based speech recognition)

## Installation Steps

### 1. Project Setup

```bash
# Clone or navigate to the project directory
cd /path/to/cheatingglasses

# Sync Gradle dependencies
./gradlew build
```

### 2. Vuzix Device Setup

1. Enable Developer Options on Vuzix Blade 2:

   - Go to Settings → About
   - Tap Build Number 7 times
   - Developer Options will appear in Settings

2. Enable USB Debugging:

   - Go to Settings → Developer Options
   - Enable "USB debugging"
   - Enable "Install via USB"

3. Connect to Computer:
   - Use USB cable to connect Vuzix Blade 2 to computer
   - Accept debugging authorization prompt on device
   - Verify connection: `adb devices`

### 3. Build and Deploy

#### Using Android Studio:

1. Open the project in Android Studio
2. Select your Vuzix Blade 2 device from the device dropdown
3. Click Run (Shift + F10) or Debug

#### Using Command Line:

```bash
# Build APK
./gradlew assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.example.cheating_glasses/.MainActivity
```

## Configuration

### 1. Permissions Configuration

The app will automatically request required permissions on first launch:

- Microphone access (RECORD_AUDIO)
- Camera access (CAMERA)
- Storage access (for saving photos)

If permissions are denied, you can grant them manually:

```bash
# Grant permissions via ADB
adb shell pm grant com.example.cheating_glasses android.permission.RECORD_AUDIO
adb shell pm grant com.example.cheating_glasses android.permission.CAMERA
```

### 2. Speech Recognition Setup

Ensure Google Speech Services is installed:

1. Open Google Play Store on Vuzix device
2. Search for "Google Speech Services"
3. Install or update if necessary

Configure speech recognition (optional):

- Go to Settings → Language & Input → Speech
- Select Google Speech Recognition
- Download offline language packs for offline use (optional)

### 3. Camera Configuration

The app uses the default back camera on Vuzix Blade 2:

- No additional configuration needed
- Photos are saved to: `/Pictures/CheatingGlasses/`
- Access photos via: Settings → Storage → Pictures

### 4. Display Optimization for Vuzix

The app is pre-configured for Vuzix Blade 2 display:

- Resolution: Optimized for 640x360 (Vuzix native)
- Font sizes: Readable on AR display
- High contrast: Black background with white/colored text

To adjust for different preferences, modify in `MainActivity.kt`:

```kotlin
// Adjust font sizes (currently 12-20sp)
fontSize = 18.sp  // Change to desired size

// Adjust colors
Color.White  // Change to preferred color
```

## Testing

### 1. Basic Functionality Test

```bash
# Check if app is installed
adb shell pm list packages | grep cheating_glasses

# View logs
adb logcat | grep -i "cheating\|speech\|camera"

# Check permissions
adb shell dumpsys package com.example.cheating_glasses | grep permission
```

### 2. Speech Recognition Test

1. Launch the app
2. Grant permissions if prompted
3. Speak clearly: "Hello world"
4. Verify text appears in transcript area
5. Speak: "solve"
6. Verify photo is captured automatically

### 3. Camera Test

1. Tap CAPTURE button
2. Check for success message
3. View captured photo:

```bash
adb shell ls /sdcard/Pictures/CheatingGlasses/
```

### 4. Pull captured photos to computer:

```bash
adb pull /sdcard/Pictures/CheatingGlasses/ ./captured_photos/
```

## Troubleshooting

### Issue: Speech recognition doesn't start

**Solutions:**

- Check internet connection
- Verify Google Speech Services is installed
- Grant microphone permission
- Restart app

```bash
# Force stop and restart
adb shell am force-stop com.example.cheating_glasses
adb shell am start -n com.example.cheating_glasses/.MainActivity
```

### Issue: Camera fails to capture

**Solutions:**

- Check storage space
- Grant camera permission
- Verify no other app is using camera

```bash
# Check available storage
adb shell df /sdcard

# Clear app data and restart
adb shell pm clear com.example.cheating_glasses
```

### Issue: Keyword "solve" not detected

**Solutions:**

- Ensure speech recognition is active (green indicator)
- Speak clearly and distinctly
- Check transcript shows your speech
- Try saying "solve" with pauses before/after

### Issue: App crashes on startup

**Solutions:**

- Check logcat for errors:

```bash
adb logcat | grep -E "AndroidRuntime|FATAL"
```

- Verify all dependencies are installed
- Clear app data and reinstall

## Debugging

### Enable verbose logging:

Add to `MainActivity.kt`:

```kotlin
Log.d("VuzixApp", "Debug message here")
```

View logs:

```bash
# Filter by app
adb logcat | grep VuzixApp

# Save logs to file
adb logcat -d > app_logs.txt
```

### Monitor performance:

```bash
# CPU and memory usage
adb shell top | grep cheating_glasses

# Battery usage
adb shell dumpsys batterystats | grep cheating_glasses
```

## Advanced Configuration

### Custom Keyword

To change the detection keyword from "solve" to something else:

1. Open `VuzixViewModel.kt`
2. Find: `KeywordDetector("solve")`
3. Change to: `KeywordDetector("your_keyword")`
4. Rebuild and deploy

### Speech Recognition Language

To change language in `SpeechRecognitionService.kt`:

```kotlin
intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")  // Spanish
intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")  // French
```

### Camera Quality Settings

Adjust in `CameraManager.kt`:

```kotlin
ImageCapture.Builder()
    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)  // or MINIMIZE_LATENCY
    .setTargetRotation(rotation)
    .build()
```

## Deployment to Production

### 1. Create Release Build

Update `build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("your-keystore.jks")
            storePassword = "your-password"
            keyAlias = "your-alias"
            keyPassword = "your-password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

Build release APK:

```bash
./gradlew assembleRelease
```

### 2. Testing Release Build

```bash
# Install release build
adb install -r app/build/outputs/apk/release/app-release.apk

# Test thoroughly before distribution
```

### 3. Distribution

Options for distributing to Vuzix Blade 2 users:

- Google Play Store (recommended)
- Enterprise app distribution
- Direct APK distribution
- Vuzix App Store

## Support Resources

### Official Documentation

- [Vuzix Blade 2 Developer Guide](https://www.vuzix.com/pages/developer)
- [Android Camera Documentation](https://developer.android.com/training/camera)
- [Android Speech Recognition](https://developer.android.com/reference/android/speech/SpeechRecognizer)

### Community

- Vuzix Developer Forums
- Stack Overflow (tag: vuzix, android-camera, speech-recognition)

### Contact

For app-specific issues, please refer to the GitHub repository or contact the development team.
