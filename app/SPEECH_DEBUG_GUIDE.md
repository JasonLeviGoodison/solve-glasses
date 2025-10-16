# Speech Recognition Debugging Guide

## ✅ Good News!

Camera is working perfectly - photos are being captured successfully! 📸

## 🐛 Issue: Speech Recognition Not Working

The speech recognition isn't starting or showing transcripts. I've added comprehensive logging to help debug this.

## What I Added

### Logging in SpeechRecognitionService

Now you'll see logs like:

```
SpeechRecognition: startListening() called
SpeechRecognition: Creating new SpeechRecognizer
SpeechRecognition: Starting speech recognition
SpeechRecognition: Ready for speech
SpeechRecognition: Beginning of speech
SpeechRecognition: Partial results: hello world
SpeechRecognition: Final results: hello world
```

Or if there are errors:

```
SpeechRecognition: Error: Insufficient permissions (code: 9)
SpeechRecognition: Error: Network error (code: 4)
```

## How to Debug

### Step 1: Run the App and Watch Logs

Run this command in terminal:

```bash
adb logcat | grep -E "SpeechRecognition|VuzixViewModel"
```

### Step 2: Check What Logs You See

#### Case 1: No Logs at All

**Problem:** Speech recognition is not being started
**Possible causes:**

- ViewModel not initializing properly
- startListening() not being called

#### Case 2: "Speech recognition not available"

**Problem:** Google Speech Services not installed on emulator
**Solution:**

```bash
# Check if Google app is installed
adb shell pm list packages | grep google
```

#### Case 3: "Error: Insufficient permissions (code: 9)"

**Problem:** Microphone permission not granted
**Solution:** Check permissions were actually granted

#### Case 4: "Ready for speech" but no results

**Problem:** Microphone not capturing audio on emulator
**Solution:** Emulator microphone might not be working

## Quick Test

### Test 1: Check if startListening() is called

Look for this log:

```
SpeechRecognition: startListening() called
```

If you DON'T see this, the issue is in ViewModel initialization.

### Test 2: Check for errors

Look for:

```
SpeechRecognition: Error: <error message>
```

### Test 3: Check permissions

Run:

```bash
adb shell dumpsys package com.example.cheating_glasses | grep "RECORD_AUDIO"
```

Should show:

```
android.permission.RECORD_AUDIO: granted=true
```

## Common Issues & Solutions

### Issue: "Speech recognition not available"

**Solution:** Install Google app on emulator

```bash
# This is usually pre-installed, but if not:
# Download Google app APK and install
```

### Issue: "Insufficient permissions"

**Solution:** Verify permissions were granted

- Check the permission request dialog appeared
- Grant RECORD_AUDIO permission
- Restart the app

### Issue: No errors but no transcripts

**Solution:** Emulator microphone issue

- Emulators don't have real microphones
- Google Speech Services might not work well on emulator
- **Test on real device for speech recognition**

## Expected Behavior on Real Device (Vuzix Blade 2)

1. App starts
2. Permissions granted
3. Logs show:
   ```
   SpeechRecognition: startListening() called
   SpeechRecognition: Creating new SpeechRecognizer
   SpeechRecognition: Starting speech recognition
   SpeechRecognition: Ready for speech
   ```
4. You speak
5. Logs show:
   ```
   SpeechRecognition: Partial results: <your speech>
   ```
6. Transcript appears on screen

## Important Notes

### About Emulators

⚠️ **Speech recognition often doesn't work well on emulators** because:

1. No real microphone
2. Google Speech Services may not be properly configured
3. Network connectivity issues

### Recommendation

✅ **Test speech recognition on actual Vuzix Blade 2 device**

- Real microphone
- Proper Google Services
- Full functionality

### Alternative: Test with Text Input

If you need to test keyword detection without speech, you could temporarily add a text input field to manually trigger the keyword detector.

## Next Steps

1. **Run the app** and check logcat for speech recognition logs
2. **Share the logs** you see (especially SpeechRecognition tags)
3. **Note any error messages**
4. If "Speech recognition not available" → Emulator issue, test on real device
5. If "Insufficient permissions" → Check permission grant dialog
6. If "Ready for speech" but no results → Emulator microphone issue

## Full Log Command

To see everything:

```bash
adb logcat | grep -E "SpeechRecognition|VuzixViewModel|CameraManager"
```

This will show:

- Speech recognition activity
- ViewModel initialization
- Camera operations (which are working ✅)

## What to Look For

### Successful Flow:

```
VuzixViewModel: Initializing with permissions
SpeechRecognition: startListening() called
SpeechRecognition: Creating new SpeechRecognizer
SpeechRecognition: Starting speech recognition
SpeechRecognition: Ready for speech
[User speaks]
SpeechRecognition: Partial results: solve this problem
KeywordDetector: Keyword "solve" detected!
CameraManager: Capturing photo...
CameraManager: Photo capture succeeded
```

### What You Have Now:

```
CameraManager: Using front camera
CameraManager: Camera initialized successfully (ImageCapture only)
CameraManager: Photo capture succeeded ✅
[Missing speech recognition logs ❌]
```

---

**Run the app now and share the SpeechRecognition logs you see!**
