package com.example.cheating_glasses

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cheating_glasses.ui.theme.CheatingglassesTheme
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    
    private val viewModel: VuzixViewModel by viewModels()
    private var permissionsGranted by mutableStateOf(false)
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions.all { it.value }
        if (permissionsGranted) {
            viewModel.initialize(this, this)
            viewModel.startListening()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        permissionsGranted = checkPermissions()
        
        setContent {
            CheatingglassesTheme {
                VuzixApp(
                    viewModel = viewModel,
                    permissionsGranted = permissionsGranted,
                    onRequestPermissions = { requestPermissions() }
                )
            }
        }
        
        if (permissionsGranted) {
            viewModel.initialize(this, this)
            viewModel.startListening()
        } else {
            requestPermissions()
        }
    }
    
    private fun checkPermissions(): Boolean {
        val audioOk = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val cameraOk = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val mediaOk = if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            val readOk = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val writeOk = if (Build.VERSION.SDK_INT <= 28) ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED else true
            readOk && writeOk
        }
        return audioOk && cameraOk && mediaOk
    }
    
    private fun requestPermissions() {
        val perms = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (Build.VERSION.SDK_INT >= 33) {
            perms += Manifest.permission.READ_MEDIA_IMAGES
        } else {
            perms += Manifest.permission.READ_EXTERNAL_STORAGE
            if (Build.VERSION.SDK_INT <= 28) {
                perms += Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        }
        requestPermissionLauncher.launch(perms.toTypedArray())
    }
}

@Composable
fun VuzixApp(
    viewModel: VuzixViewModel,
    permissionsGranted: Boolean,
    onRequestPermissions: () -> Unit
) {
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        if (!permissionsGranted) {
            PermissionScreen(
                onRequestPermissions = onRequestPermissions
            )
        } else {
            AIOnlyScreen(text = aiResponse)
        }
    }
}
@Composable
fun AIOnlyScreen(text: String) {
    val scrollState = rememberScrollState()
    var containerH by remember { mutableStateOf(0) }
    var contentH by remember { mutableStateOf(0) }

    LaunchedEffect(text) { scrollState.scrollTo(0) }

    LaunchedEffect(containerH, contentH, text) {
        if (contentH > containerH && containerH > 0) {
            kotlinx.coroutines.delay(10_000)
            var pos = 0
            val stepPx = 1
            while (pos < scrollState.maxValue) {
                pos += stepPx
                scrollState.scrollTo(pos.coerceAtMost(scrollState.maxValue))
                kotlinx.coroutines.delay(80)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp)
            .onSizeChanged { containerH = it.height }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = if (text.isNotEmpty()) text else "Waiting for AI response...",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords -> contentH = coords.size.height }
            )
        }
    }
}

@Composable
fun PermissionScreen(onRequestPermissions: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Permissions Required",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app needs microphone and camera permissions to function.",
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestPermissions) {
            Text("Grant Permissions")
        }
    }
}

@Composable
fun VuzixMainScreen(
    transcript: String,
    isListening: Boolean,
    keywordDetected: Boolean,
    statusMessage: String,
    captureStatus: com.example.cheating_glasses.utils.CameraManager.CaptureStatus,
    lastImageUri: String?,
    onStopListening: () -> Unit,
    onStartListening: () -> Unit,
    onCapturePhoto: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            // Show last captured image prominently at the top
            if (!lastImageUri.isNullOrEmpty()) {
                Text(
                    text = "LAST CAPTURE:",
                    color = Color.Cyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = lastImageUri,
                    contentDescription = "Last captured",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (statusMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            statusMessage.contains("Error") -> Color.Red.copy(alpha = 0.3f)
                            statusMessage.contains("success") -> Color.Green.copy(alpha = 0.3f)
                            keywordDetected -> Color.Yellow.copy(alpha = 0.3f)
                            else -> Color.Blue.copy(alpha = 0.3f)
                        }
                    )
                ) {
                    Text(
                        text = statusMessage,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Image already shown above transcript when available
        }
        
        HorizontalDivider(
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}