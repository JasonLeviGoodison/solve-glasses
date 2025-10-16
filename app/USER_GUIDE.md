# Vuzix Blade 2 Smart Glasses - User Guide

## 📱 Getting Started

### First Time Setup

1. **Install the App**

   - Download and install the app on your Vuzix Blade 2
   - Tap the app icon to launch

2. **Grant Permissions**

   - When prompted, tap "Grant Permissions"
   - Allow microphone access (for speech recognition)
   - Allow camera access (for photo capture)
   - Allow storage access (for saving photos)

3. **Start Using**
   - The app will automatically start listening
   - You'll see "● LISTENING" indicator turn green
   - Start speaking to see live transcripts

## 🎤 Using Speech-to-Text

### How It Works

The app continuously listens to your surroundings and displays what it hears in real-time on your Vuzix display.

### What You'll See

```
┌─────────────────────────────────┐
│  Vuzix Smart Glasses            │
│  ────────────────────────────   │
│  ● LISTENING                    │
│                                 │
│  LIVE TRANSCRIPT:               │
│  ┌───────────────────────────┐ │
│  │                           │ │
│  │  "Hello, how are you      │ │
│  │   doing today?"           │ │
│  │                           │ │
│  └───────────────────────────┘ │
│                                 │
│  [STOP]        [CAPTURE]        │
│                                 │
│  Say 'solve' to auto-capture    │
└─────────────────────────────────┘
```

### Tips for Best Results

- **Speak clearly** at normal volume
- **Stay within 6 feet** of speakers
- **Minimize background noise** when possible
- **Wait for transcript** to appear before speaking again

## 📸 Capturing Photos

### Method 1: Voice Command (Automatic)

Simply say the word **"solve"** during conversation:

```
You say: "Let me solve this problem..."
         ↓
App detects "solve"
         ↓
🔴 KEYWORD DETECTED alert appears
         ↓
Photo automatically captured
         ↓
✅ "Photo captured and saved!" message
```

### Method 2: Manual Capture

Tap the **[CAPTURE]** button on screen

### Where Photos Are Saved

- Location: `Pictures/CheatingGlasses/` folder
- Format: JPEG
- Naming: `yyyy-MM-dd-HH-mm-ss-SSS.jpg`
- Quality: High quality (maximum)

### Accessing Your Photos

1. Open the Gallery app on Vuzix
2. Navigate to Albums
3. Find "CheatingGlasses" album
4. View your captured photos

## 🎮 Controls & Interface

### Status Indicators

| Indicator              | Meaning                   |
| ---------------------- | ------------------------- |
| ● LISTENING (Green)    | App is actively listening |
| ○ NOT LISTENING (Gray) | Speech recognition paused |
| 🔴 KEYWORD DETECTED    | "Solve" was detected      |
| Blue Card              | General status message    |
| Green Card             | Success notification      |
| Red Card               | Error message             |

### Buttons

**[START] / [STOP]** Button

- Green = Click to start listening
- Red = Click to stop listening
- Use when you want to pause/resume speech recognition

**[CAPTURE]** Button

- Takes a photo immediately
- Use for manual photo capture
- Disabled while a photo is being captured

## 📋 Common Usage Scenarios

### Scenario 1: Lecture or Meeting

```
1. Launch app at start of lecture
2. App automatically starts listening
3. View live transcripts as people speak
4. Say "solve" when you see a problem to capture
5. Review photos later
```

### Scenario 2: Reading Text or Signs

```
1. Ensure app is listening
2. Read text aloud OR have someone read it
3. Say "solve" when ready to capture
4. Photo is taken automatically
5. Access photo from gallery
```

### Scenario 3: Quick Photo Capture

```
1. Open app
2. Tap [CAPTURE] button
3. Photo taken immediately
4. No voice command needed
```

## ⚙️ Settings & Customization

### Adjusting Display Brightness

If text is hard to read:

1. Swipe down from top of Vuzix display
2. Tap Settings ⚙️
3. Select Display
4. Increase brightness

### Managing Permissions

To review or change permissions:

1. Go to Vuzix Settings
2. Apps → CheatingGlasses
3. Permissions
4. Toggle as needed

### Clearing Stored Photos

To free up space:

1. Open Gallery
2. Go to CheatingGlasses album
3. Select photos to delete
4. Tap delete icon

## 🔋 Battery Management

### Battery Life

- **Continuous use**: ~2-3 hours
- **With display off**: ~4 hours
- **Standby**: Minimal drain

### Extending Battery Life

1. **Stop listening when not needed**

   - Tap [STOP] to pause recognition
   - Reduces battery consumption

2. **Lower brightness**

   - Darker display uses less power
   - Adjust in Vuzix settings

3. **Close app when finished**
   - Swipe up from bottom
   - Swipe app away

### Low Battery Warning

When battery drops below 20%, consider:

- Saving important photos
- Stopping speech recognition
- Connecting to charger

## 🔊 Audio & Microphone

### Microphone Quality

- Vuzix has excellent built-in mics
- Picks up speech from ~6 feet away
- Good noise cancellation

### For Best Audio Recognition

- **Position yourself**: Face the speaker
- **Reduce noise**: Move away from loud areas
- **Check connection**: Ensure internet connection (required for speech recognition)

### If Speech Recognition Fails

1. Check internet connection
2. Ensure microphone isn't blocked
3. Tap [STOP] then [START] to restart
4. Restart app if issues persist

## 🛠️ Troubleshooting

### Problem: App won't start listening

**Solutions:**

- Check if microphone permission is granted
- Verify internet connection is active
- Restart the app
- Ensure Google Speech Services is installed

### Problem: Transcript not appearing

**Solutions:**

- Speak louder or more clearly
- Check "● LISTENING" indicator is green
- Move closer to sound source
- Restart speech recognition (STOP → START)

### Problem: "Solve" not triggering photo

**Solutions:**

- Ensure you're pronouncing "solve" clearly
- Check transcript shows the word
- Verify camera permission is granted
- Try manual [CAPTURE] button

### Problem: Photos not saving

**Solutions:**

- Check storage space available
- Verify storage permissions granted
- Check if camera is working (test with manual capture)
- Restart app

### Problem: App is slow or laggy

**Solutions:**

- Close other apps
- Restart Vuzix device
- Clear app cache (Settings → Apps → CheatingGlasses → Clear Cache)
- Ensure sufficient battery

## 🔒 Privacy & Security

### What the App Accesses

- **Microphone**: Only when actively listening
- **Camera**: Only when capturing photos
- **Storage**: Only to save photos you capture

### Your Data

- **Speech data**: Processed by Google Speech Services
- **Photos**: Stored locally on your device only
- **No cloud upload**: Photos stay on your device
- **No data sharing**: App doesn't share your data

### Privacy Tips

- Tap [STOP] when you don't want the app listening
- Delete photos you don't need
- Close app when not in use
- Review permissions regularly

## 📊 Understanding the Display

### Main Screen Layout

```
╔═══════════════════════════════════╗
║  Vuzix Smart Glasses              ║  ← Title
║  ─────────────────────────────    ║  ← Divider
║  ● LISTENING    🔴 KEYWORD        ║  ← Status Row
║                                   ║
║  LIVE TRANSCRIPT:                 ║  ← Label
║  ┌─────────────────────────────┐ ║
║  │                             │ ║
║  │   Your speech appears here  │ ║  ← Transcript Area
║  │   in real-time as you speak │ ║
║  │                             │ ║
║  └─────────────────────────────┘ ║
║                                   ║
║  ┌─────────────────────────────┐ ║
║  │ Listening for speech...     │ ║  ← Status Message
║  └─────────────────────────────┘ ║
║  ─────────────────────────────    ║
║                                   ║
║     [STOP]        [CAPTURE]       ║  ← Control Buttons
║                                   ║
║  Say 'solve' to auto-capture      ║  ← Help Text
╚═══════════════════════════════════╝
```

### Color Meanings

- **White**: Normal text, readable
- **Cyan**: Labels and headers
- **Green**: Active/success states
- **Red**: Alerts and errors
- **Gray**: Inactive states
- **Yellow**: Warnings
- **Blue**: Information

## 🎯 Pro Tips

### Tip 1: Continuous Recognition

The app automatically restarts listening after detecting speech. You don't need to do anything - just keep talking!

### Tip 2: Partial Results

You'll see words appear as you speak them, not just when you finish. This gives you instant feedback.

### Tip 3: Keyword Anywhere

"Solve" can appear anywhere in your sentence:

- "Let me **solve** this"
- "We need to **solve** the problem"
- "The **solve** is simple"

All will trigger the photo capture!

### Tip 4: Quick Restart

If speech recognition stops working:

- Quick fix: Tap STOP, then START
- This restarts the service

### Tip 5: Hands-Free Operation

Once started, the app runs completely hands-free. Just speak and say "solve" when you want a photo. Perfect for Vuzix!

## 📞 Getting Help

### If You Need Assistance

1. Review this guide
2. Check Troubleshooting section
3. Restart the app
4. Restart Vuzix device
5. Contact support

### Reporting Issues

When reporting problems, note:

- What you were trying to do
- What happened instead
- Any error messages shown
- Steps to reproduce the issue

## 🔄 Updates & Maintenance

### Checking for Updates

1. Open Google Play Store
2. Search for the app
3. Tap Update if available

### Keeping App Running Smoothly

- Update regularly
- Clear cache monthly
- Delete old photos to free space
- Restart Vuzix weekly

### What's New (Version 1.0)

- ✨ Real-time speech-to-text
- 🎤 Voice-triggered photo capture
- 📸 High-quality image saving
- 🎨 Vuzix-optimized interface
- 🔋 Battery-efficient operation

## ✅ Quick Reference

### Essential Commands

| Action                 | How To                         |
| ---------------------- | ------------------------------ |
| Start listening        | App auto-starts or tap [START] |
| Stop listening         | Tap [STOP]                     |
| Capture photo (voice)  | Say "solve"                    |
| Capture photo (manual) | Tap [CAPTURE]                  |
| View photos            | Open Gallery → CheatingGlasses |

### Status Checks

- ✅ Green dot = Listening active
- ✅ Red alert = Keyword detected
- ✅ Blue message = Status update
- ✅ Green message = Success
- ✅ Red message = Error

### Quick Fixes

- **Not listening?** → Tap START
- **No transcript?** → Speak louder
- **No photo?** → Check permissions
- **App frozen?** → Restart app

---

## 🎉 You're Ready!

You now know how to use the Vuzix Blade 2 Smart Glasses app. Start by:

1. ✅ Launching the app
2. ✅ Granting permissions
3. ✅ Speaking to see transcripts
4. ✅ Saying "solve" to capture photos

Enjoy your hands-free smart glasses experience! 👓
