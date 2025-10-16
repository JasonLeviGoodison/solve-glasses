package com.example.cheating_glasses.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(private val context: Context) {
    
    private var imageCapture: ImageCapture? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    
    private val _lastCapturedImageUri = MutableStateFlow<String?>(null)
    val lastCapturedImageUri: StateFlow<String?> = _lastCapturedImageUri.asStateFlow()
    
    private val _captureStatus = MutableStateFlow<CaptureStatus>(CaptureStatus.Idle)
    val captureStatus: StateFlow<CaptureStatus> = _captureStatus.asStateFlow()
    
    sealed class CaptureStatus {
        object Idle : CaptureStatus()
        object Capturing : CaptureStatus()
        data class Success(val uri: String) : CaptureStatus()
        data class Error(val message: String) : CaptureStatus()
    }
    
    fun initializeCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: androidx.camera.view.PreviewView? = null
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()
                
                cameraProvider.unbindAll()
                
                val cameraSelector = selectAvailableCamera(cameraProvider)
                
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageCapture
                )
                
                Log.d("CameraManager", "Camera initialized successfully (ImageCapture only)")
            } catch (e: Exception) {
                Log.e("CameraManager", "Camera initialization failed", e)
                _captureStatus.value = CaptureStatus.Error("Camera initialization failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }
    
    private fun selectAvailableCamera(cameraProvider: ProcessCameraProvider): CameraSelector {
        val availableCameras = cameraProvider.availableCameraInfos
        
        if (availableCameras.isEmpty()) {
            throw IllegalStateException("No cameras available on this device")
        }
        
        return when {
            cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> {
                Log.d("CameraManager", "Using back camera")
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> {
                Log.d("CameraManager", "Using front camera (back camera not available)")
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
            else -> {
                Log.w("CameraManager", "No default camera found, using first available")
                val firstCamera = availableCameras.first()
                val lensFacing = when (firstCamera.lensFacing) {
                    CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_FRONT
                    CameraSelector.LENS_FACING_BACK -> CameraSelector.LENS_FACING_BACK
                    else -> CameraSelector.LENS_FACING_FRONT
                }
                CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()
            }
        }
    }
    
    fun capturePhoto(onSuccess: (String) -> Unit = {}, onError: (String) -> Unit = {}) {
        val imageCapture = imageCapture ?: run {
            val error = "Camera not initialized"
            _captureStatus.value = CaptureStatus.Error(error)
            onError(error)
            return
        }
        
        _captureStatus.value = CaptureStatus.Capturing
        
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CheatingGlasses")
            }
        }
        
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
        
        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    val errorMessage = "Photo capture failed: ${exc.message}"
                    Log.e("CameraManager", errorMessage, exc)
                    _captureStatus.value = CaptureStatus.Error(errorMessage)
                    onError(errorMessage)
                }
                
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri?.toString() ?: ""
                    _lastCapturedImageUri.value = savedUri
                    _captureStatus.value = CaptureStatus.Success(savedUri)
                    Log.d("CameraManager", "Photo capture succeeded: $savedUri")
                    onSuccess(savedUri)
                }
            }
        )
    }
    
    fun shutdown() {
        cameraExecutor.shutdown()
    }
}

