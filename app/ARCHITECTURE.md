# Application Architecture

## Overview

This document describes the architecture and data flow of the Vuzix Blade 2 Smart Glasses application.

## Architecture Pattern: MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────────────────────────┐
│                     MainActivity (View)                  │
│  ┌────────────────────────────────────────────────┐    │
│  │         Jetpack Compose UI Components           │    │
│  │  • VuzixApp                                      │    │
│  │  • PermissionScreen                              │    │
│  │  • VuzixMainScreen                               │    │
│  └────────────────────────────────────────────────┘    │
└────────────────────────┬────────────────────────────────┘
                         │ observes StateFlow
                         ▼
┌─────────────────────────────────────────────────────────┐
│                  VuzixViewModel                          │
│  ┌────────────────────────────────────────────────┐    │
│  │  State Management (Kotlin Flow)                 │    │
│  │  • transcript: StateFlow<String>                │    │
│  │  • isListening: StateFlow<Boolean>              │    │
│  │  • keywordDetected: StateFlow<Boolean>          │    │
│  │  • lastCapturedImage: StateFlow<String?>        │    │
│  │  • statusMessage: StateFlow<String>             │    │
│  └────────────────────────────────────────────────┘    │
│  ┌────────────────────────────────────────────────┐    │
│  │  Business Logic                                  │    │
│  │  • initialize()                                  │    │
│  │  • startListening()                              │    │
│  │  • stopListening()                               │    │
│  │  • capturePhoto()                                │    │
│  └────────────────────────────────────────────────┘    │
└────────────────────────┬────────────────────────────────┘
                         │ coordinates
                         ▼
┌─────────────────────────────────────────────────────────┐
│                    Utility Services                      │
│                                                          │
│  ┌──────────────────┐ ┌──────────────┐ ┌────────────┐  │
│  │  SpeechRecognition│ │   Keyword    │ │  Camera    │  │
│  │     Service       │ │   Detector   │ │  Manager   │  │
│  └──────────────────┘ └──────────────┘ └────────────┘  │
└─────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│                Android System APIs                       │
│  • SpeechRecognizer    • MediaStore                     │
│  • CameraX             • ContentResolver                │
└─────────────────────────────────────────────────────────┘
```

## Component Responsibilities

### 1. View Layer (MainActivity.kt)

**Responsibilities:**

- Render UI using Jetpack Compose
- Handle user interactions
- Request and manage permissions
- Observe ViewModel state changes
- Display real-time updates

**Key Composables:**

```
VuzixApp
├── PermissionScreen (if permissions not granted)
└── VuzixMainScreen (main interface)
    ├── Status indicators
    ├── Transcript display
    ├── Action buttons
    └── Status messages
```

### 2. ViewModel Layer (VuzixViewModel.kt)

**Responsibilities:**

- Manage application state
- Coordinate between services
- Handle business logic
- Expose state via StateFlow
- Manage service lifecycle

**State Flow:**

```
User Action → ViewModel Method → Service Call → State Update → UI Refresh
```

### 3. Service Layer (utils/)

#### SpeechRecognitionService

**Responsibilities:**

- Initialize Android SpeechRecognizer
- Manage continuous listening
- Process speech results
- Handle errors and restarts
- Emit transcript updates

#### KeywordDetector

**Responsibilities:**

- Monitor incoming text
- Detect "solve" keyword
- Trigger callbacks on detection
- Debounce duplicate detections

#### CameraManager

**Responsibilities:**

- Initialize CameraX
- Capture photos
- Save to MediaStore
- Manage camera lifecycle
- Report capture status

## Data Flow Diagrams

### Speech-to-Text Flow

```
User Speaks
    │
    ▼
Microphone Input
    │
    ▼
SpeechRecognizer (Android API)
    │
    ▼
onPartialResults() / onResults()
    │
    ▼
SpeechRecognitionService
    │
    ▼
_transcriptFlow.emit(text)
    │
    ▼
VuzixViewModel.transcript
    │
    ▼
UI Updates (Compose recomposition)
    │
    ▼
Display on Screen
```

### Keyword Detection & Photo Capture Flow

```
Speech Text Available
    │
    ▼
SpeechRecognitionService.onPartialResults
    │
    ▼
KeywordDetector.processText(text)
    │
    ├─ No Match → Continue listening
    │
    └─ "solve" detected
        │
        ▼
    onKeywordDetected callback
        │
        ▼
    VuzixViewModel.capturePhoto()
        │
        ▼
    CameraManager.capturePhoto()
        │
        ▼
    ImageCapture.takePicture()
        │
        ▼
    Save to MediaStore
        │
        ▼
    onImageSaved callback
        │
        ▼
    _lastCapturedImageUri.emit(uri)
        │
        ▼
    VuzixViewModel state update
        │
        ▼
    UI shows success message
```

## State Management

### StateFlow Architecture

```kotlin
// ViewModel exposes immutable state
private val _transcript = MutableStateFlow("")
val transcript: StateFlow<String> = _transcript.asStateFlow()

// UI collects and reacts to state
val transcript by viewModel.transcript.collectAsStateWithLifecycle()

// Service updates state
_transcript.value = newText
```

### State Synchronization

```
Service Layer (MutableStateFlow)
    │
    │ emit()
    ▼
ViewModel Layer (StateFlow)
    │
    │ collect()
    ▼
UI Layer (Compose State)
    │
    │ recomposition
    ▼
Display Updates
```

## Lifecycle Management

### App Lifecycle

```
MainActivity.onCreate()
    │
    ▼
Initialize ViewModel
    │
    ▼
Check Permissions
    │
    ├─ Denied → Show PermissionScreen
    │
    └─ Granted
        │
        ▼
    viewModel.initialize()
        │
        ▼
    Create Services
        │
        ▼
    viewModel.startListening()
        │
        ▼
    App Running
        │
        ▼
    MainActivity.onDestroy()
        │
        ▼
    viewModel.onCleared()
        │
        ▼
    Cleanup Services
```

### Service Lifecycle

```kotlin
// Initialization
initialize(context, lifecycleOwner) {
    speechRecognitionService = SpeechRecognitionService(context)
    keywordDetector = KeywordDetector("solve")
    cameraManager = CameraManager(context)

    // Setup observers and callbacks
    collectFlows()
}

// Runtime
startListening() → Service Running → Auto-restart on error

// Cleanup
onCleared() {
    speechRecognitionService.destroy()
    cameraManager.shutdown()
}
```

## Threading Model

```
Main Thread (UI)
    │
    ├─ Compose UI rendering
    ├─ User interactions
    └─ StateFlow collection

Background Threads
    │
    ├─ Speech Recognition (System managed)
    ├─ Camera Operations (CameraExecutor)
    └─ Coroutines (viewModelScope)
        │
        ├─ Dispatchers.Main → UI updates
        ├─ Dispatchers.IO → File operations
        └─ Dispatchers.Default → CPU work
```

## Error Handling Strategy

### Speech Recognition Errors

```
Error Detected
    │
    ▼
SpeechRecognitionService.onError()
    │
    ├─ Network/Server Error
    │   └─ Restart after delay
    │
    ├─ No Match / Timeout
    │   └─ Immediate restart
    │
    └─ Permission Error
        └─ Emit error state, stop service
```

### Camera Errors

```
Capture Failure
    │
    ▼
CameraManager.onError()
    │
    ▼
Log error
    │
    ▼
Update _captureStatus
    │
    ▼
ViewModel emits error message
    │
    ▼
UI displays error to user
```

## Dependency Graph

```
MainActivity
    │
    └── VuzixViewModel
            │
            ├── SpeechRecognitionService
            │       └── Android SpeechRecognizer
            │
            ├── KeywordDetector
            │       └── String processing
            │
            └── CameraManager
                    └── CameraX API
                        └── MediaStore
```

## Key Design Decisions

### 1. Reactive State with Kotlin Flow

**Why:** Provides clean, lifecycle-aware state management perfect for Compose

### 2. MVVM Architecture

**Why:** Separates concerns, testable, follows Android best practices

### 3. Continuous Speech Recognition

**Why:** Hands-free operation critical for AR glasses use case

### 4. Auto-restart on Errors

**Why:** Ensures continuous functionality without user intervention

### 5. StateFlow over LiveData

**Why:** Better Kotlin integration, works seamlessly with Coroutines

### 6. Single ViewModel

**Why:** Simple coordination between services, single source of truth

### 7. CameraX over Camera2

**Why:** Simpler API, lifecycle-aware, better compatibility

## Performance Considerations

### Memory Optimization

- Services created only when needed
- Proper cleanup in ViewModel.onCleared()
- No large object retention in state
- Efficient StateFlow usage (shareIn, stateIn)

### Battery Optimization

- Dark UI theme (OLED power saving)
- Efficient speech recognition (auto-restart delays)
- CameraX lifecycle binding
- No continuous camera preview (capture only)

### UI Performance

- Compose with remember and derivedStateOf
- Minimal recompositions
- Efficient state collection with collectAsStateWithLifecycle
- No heavy operations on main thread

## Testing Strategy

### Unit Tests

- ViewModel logic
- KeywordDetector text processing
- State transformations

### Integration Tests

- Service coordination
- Flow emissions
- Lifecycle handling

### UI Tests

- Compose UI testing
- Permission flows
- User interactions

### Device Testing

- Actual Vuzix Blade 2 hardware
- Various lighting conditions
- Different noise environments
- Battery drain testing

## Future Enhancements

### Planned Improvements

1. Offline speech recognition
2. Custom wake word models
3. Vuzix SDK gesture controls
4. OCR on captured images
5. Cloud sync capabilities
6. Multiple keyword support
7. Voice command expansion
8. Advanced camera features (burst mode, filters)

### Architecture Evolution

```
Current: Single ViewModel, Simple Services
    │
    ▼
Future: Multi-module, Feature-based
    │
    ├── :feature:speech (Speech recognition module)
    ├── :feature:camera (Camera module)
    ├── :feature:ocr (OCR processing module)
    ├── :core:data (Data layer)
    └── :core:ui (Shared UI components)
```

## Conclusion

The application follows modern Android architecture patterns with:

- Clean separation of concerns
- Reactive state management
- Lifecycle-aware components
- Optimized for Vuzix Blade 2 constraints
- Maintainable and testable codebase
