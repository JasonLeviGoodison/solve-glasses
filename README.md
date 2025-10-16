# Solve Glasses

> An AR assistant for Vuzix smart glasses powered by OpenAI

## ⚠️ Educational Purpose Only

This project is meant purely for educational purposes and serves as a proof-of-concept demonstration. It showcases how to integrate multiple sensors and AI capabilities into AR hardware.
Its basically foreshadowing a huge problem that no one is thinking about

## Overview

Solve Glasses is an Android application that transforms Vuzix smart glasses into an AI-powered problem solver. By simply saying the keyword **"solve"**, users can leverage the device's integrated sensors (mic, camera, visual display) to get real-time insights about any problem.

## How It Works

**The app combines three key components:**

- **Microphone Input** - Builds up context on conversation and then will run AI when it hears the "solve" voice command
- **Camera Feed** - Captures the wearer's point of view
- **Display Output** - Shows AI-generated responses directly in the headset. There is little light bleed so the wearer is the only one that can see it

When the keyword is detected, all captured context (audio, video, and environmental data) is sent to OpenAI's API for processing. The response is then displayed with a smooth scrolling animation so users can comfortably read the AI's analysis.

## Technical Stack

- **Platform:** Android
- **Hardware:** Vuzix Smart Glasses (Blade 2)
- **AI Service:** OpenAI API
- **Language:** Java/Kotlin


### How to run
Follow the Vuzix set up guide and get your glasses running through android studio. Then, you need to copy a small Volk transcription model onto the device
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

## Use Cases for Learning

This project is ideal for developers wanting to explore:

- AR application development
- Voice command integration
- API integration in edge devices
- Real-time display rendering
- Basically smart glasses tech

---

*This is a proof-of-concept project shared for educational reference. Not intended for commercial use.*
