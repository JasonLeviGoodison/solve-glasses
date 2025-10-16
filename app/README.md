# Vuzix Blade 2 Smart Glasses Application

A real-time speech-to-text and voice-triggered photo capture application designed for Vuzix Blade 2 smart glasses.

## Features

### 1. Real-Time Speech-to-Text Display

- Continuous audio capture from the device microphone
- Live transcription of spoken words displayed on screen
- Automatic restart of speech recognition for continuous listening
- Optimized for hands-free operation on Vuzix Blade 2

### 2. Voice-Triggered Photo Capture

- Keyword detection for the word "solve"
- Automatic photo capture when keyword is detected
- Images saved to device gallery in `Pictures/CheatingGlasses` folder
- Visual feedback on keyword detection and capture status

## Architecture

### Components

#### 1. **SpeechRecognitionService** (`utils/SpeechRecognitionService.kt`)

- Manages Android SpeechRecognizer
- Provides continuous listening with auto-restart
- Exposes transcript and listening state via StateFlow
- Handles partial results for real-time updates

#### 2. **KeywordDetector** (`utils/KeywordDetector.kt`)

- Processes incoming text for keyword detection
- Case-insensitive matching for "solve"
- Callback-based notification system
- Prevents duplicate triggers with debouncing

#### 3. **CameraManager** (`utils/CameraManager.kt`)

- Integrates with CameraX API
- Handles photo capture and storage
- Saves images to MediaStore
- Provides capture status updates

#### 4. **VuzixViewModel** (`VuzixViewModel.kt`)

- Coordinates all services
- Manages app state with Kotlin Flows
- Handles lifecycle of services
- Orchestrates keyword detection → photo capture flow

#### 5. **MainActivity** (`MainActivity.kt`)

- Jetpack Compose UI optimized for Vuzix display
- Permission handling with Accompanist
- Real-time UI updates using StateFlow
- Dark theme optimized for AR glasses

## Permissions

The app requires the following permissions:

- `RECORD_AUDIO` - For speech recognition
- `CAMERA` - For photo capture
- `WRITE_EXTERNAL_STORAGE` (Android 9 and below)
- `READ_MEDIA_IMAGES` (Android 13+)

Permissions are requested automatically on app launch.

## UI Design

The UI is optimized for Vuzix Blade 2 with:

- **Dark background** to reduce eye strain
- **Large, readable text** (18sp for transcripts)
- **High contrast colors** for visibility
- **Minimal, focused layout** suitable for AR display
- **Status indicators** for listening and keyword detection
- **Visual feedback** for all actions

### UI Elements:

- **Header**: App title and divider
- **Status Row**: Listening indicator and keyword detection alert
- **Transcript Card**: Live speech-to-text display
- **Status Card**: Action feedback messages
- **Control Buttons**: START/STOP listening and manual CAPTURE
- **Help Text**: Instruction for voice trigger

## How to Use

### Setup

1. Build and install the app on your Vuzix Blade 2 device
2. Grant microphone and camera permissions when prompted
3. The app will automatically start listening for speech

### Voice Commands

- **Speak normally**: Your words will appear in the transcript area
- **Say "solve"**: Automatically triggers photo capture
- **Manual capture**: Tap the CAPTURE button

### Controls

- **START/STOP Button**: Toggle speech recognition on/off
- **CAPTURE Button**: Manually capture a photo

## Technical Details

### Dependencies

- **Jetpack Compose**: Modern UI toolkit
- **CameraX**: Camera integration
- **Android SpeechRecognizer**: Speech-to-text
- **Kotlin Coroutines & Flow**: Reactive state management
- **Accompanist Permissions**: Permission handling
- **Lifecycle ViewModel**: State preservation

### Speech Recognition Features

- Continuous listening with automatic restart
- Partial results for real-time updates
- Error handling with automatic recovery
- 2-second silence detection

### Camera Features

- High-quality image capture
- MediaStore integration
- Timestamp-based file naming
- External storage with proper scoping

### Keyword Detection

- Case-insensitive matching
- Substring detection within transcript
- 1-second debounce to prevent duplicates
- Immediate visual and functional feedback

## Vuzix Blade 2 Optimizations

1. **Display**:

   - Dark theme reduces battery consumption
   - High contrast improves readability on AR display
   - Compact layout fits small screen real estate

2. **Performance**:

   - Efficient state management with Flows
   - Background thread processing for camera operations
   - Optimized speech recognition with quick restarts

3. **Hands-Free Operation**:
   - Voice-triggered capture for hands-free use
   - Continuous speech recognition
   - Minimal manual interaction required

## File Structure

```
app/src/main/java/com/example/cheating_glasses/
├── MainActivity.kt                    # Main entry point with UI
├── VuzixViewModel.kt                  # State management and coordination
└── utils/
    ├── SpeechRecognitionService.kt    # Speech-to-text service
    ├── KeywordDetector.kt             # Keyword detection logic
    └── CameraManager.kt               # Camera capture management
```

## Future Enhancements

Potential improvements for production:

- Custom wake word models for better accuracy
- Vuzix SDK integration for gesture controls
- On-device OCR for captured images
- Cloud sync for captured photos
- Multiple keyword support
- Voice feedback through Vuzix speakers
- Battery optimization modes
- Offline speech recognition

## Troubleshooting

### Speech Recognition Not Working

- Ensure microphone permission is granted
- Check that device has Google Speech Services installed
- Verify internet connection (required for Google Speech API)
- Restart the app if recognition stops

### Camera Not Capturing

- Verify camera permission is granted
- Ensure sufficient storage space
- Check that camera is not being used by another app
- Review logcat for detailed error messages

### Keyword Not Detected

- Speak clearly and at normal pace
- Ensure "solve" is pronounced distinctly
- Check that speech recognition is active (green indicator)
- Try manual capture button as fallback

## Development Notes

- Built with Kotlin and Jetpack Compose
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 36
- Uses modern Android architecture components
- Follows MVVM architecture pattern
- Reactive UI with StateFlow and Compose
