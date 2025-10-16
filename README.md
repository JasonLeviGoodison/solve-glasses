# Solve Glasses

> An AR assistant for Vuzix smart glasses powered by OpenAI

## ⚠️ Educational Purpose Only

This project is meant purely for educational purposes and serves as a proof-of-concept demonstration. It showcases how to integrate multiple sensors and AI capabilities into AR hardware.
Its basically foreshadowing a huge problem that no one is thinking about

## Overview

Solve Glasses is an Android application that transforms Vuzix smart glasses into an AI-powered problem solver. By simply saying the keyword **"solve"**, users can leverage the device's integrated sensors (mic, camera, visual display) to get real-time insights about any problem.

## How It Works

**The app combines three key components:**

- **Microphone Input** - Listens for the "solve" voice command
- **Camera Feed** - Captures the wearer's point of view
- **Display Output** - Shows AI-generated responses directly in the headset

When the keyword is detected, all captured context (audio, video, and environmental data) is sent to OpenAI's API for processing. The response is then displayed with a smooth scrolling animation so users can comfortably read the AI's analysis.

## Key Features

- 🎤 Voice activation via keyword detection
- 📹 Real-time visual context capture
- 🤖 OpenAI integration for intelligent responses
- 📜 Scrolling display for easy readability
- 👓 Native Vuzix headset support

## Technical Stack

- **Platform:** Android
- **Hardware:** Vuzix Smart Glasses
- **AI Service:** OpenAI API
- **Language:** Java/Kotlin


### How to run
Follow the Vuzix set up guide and get your glasses running through android studio. Then, you need to copy the Volk transcription onto the device
- Vuzix has a keyword tracker more or less, but we needed every word transcribed and this is the easiest way to do it

To download it, do this
```
cd ~/Downloads
curl -L -o vosk-model-small-en-us-0.15.zip https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip
unzip vosk-model-small-en-us-0.15.zip
```

Then this
```
adb shell mkdir -p /sdcard/Android/data/com.example.cheating_glasses/files
adb push ~/Downloads/vosk-model-small-en-us-0.15 /sdcard/Android/data/com.example.cheating_glasses/files/
adb shell ls -la /sdcard/Android/data/com.example.cheating_glasses/files/
```

Then run this and watch the logs:
```
adb logcat | grep -E "VoskTranscriber|Transcript|VuzixVM"
```

## Why This Won't Work in Production

While conceptually interesting, this approach has several real-world limitations:

- Constant API calls create latency issues
- Privacy concerns with continuous context capture
- Battery drain from constant sensor monitoring
- Requires reliable internet connectivity
- May not work reliably in varied lighting conditions

## Use Cases for Learning

This project is ideal for developers wanting to explore:

- AR application development
- Voice command integration
- Sensor fusion in mobile apps
- API integration in edge devices
- Real-time display rendering

---

*This is a proof-of-concept project shared for educational reference. Not intended for commercial use.*
