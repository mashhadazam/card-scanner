package com.lemon.cardscanner.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.lemon.cardscanner.core.CardConfig
import com.lemon.cardscanner.core.CardMatcher
import com.lemon.cardscanner.core.ScanResult
import java.util.concurrent.Executors

/**
 * Camera card scan. OCR runs on-device via ML Kit; the photo never leaves
 * the phone and only the BIN + last 4 digits are kept in memory.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    cards: List<CardConfig>,
    onIdentified: (ScanResult) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    var status by remember { mutableStateOf("Point the camera at the card, then tap Capture.") }
    var capturing by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember {
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    DisposableEffect(hasPermission) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return@DisposableEffect onDispose {}
        }
        val provider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }
        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture
        )
        onDispose {
            provider.unbindAll()
            cameraExecutor.shutdown()
            recognizer.close()
        }
    }

    fun capture() {
        capturing = true
        status = "Reading card…"
        scanResult = null
        imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val mediaImage = image.image
                if (mediaImage == null) {
                    image.close()
                    capturing = false
                    status = "Capture failed — try again."
                    return
                }
                val input = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                recognizer.process(input)
                    .addOnSuccessListener { visionText ->
                        val text = visionText.text
                        val numbers = CardMatcher.extractCandidates(text)
                        if (numbers.isEmpty()) {
                            status = "No card number found — hold the card flat and well lit, then retry."
                        } else {
                            val number = numbers.first()
                            val matched = CardMatcher.matchBin(
                                number,
                                CardMatcher.buildBinIndex(cards)
                            )
                            scanResult = ScanResult(
                                bin = number.take(6),
                                last4 = number.takeLast(4),
                                expiry = CardMatcher.extractExpiry(text),
                                holderName = CardMatcher.extractHolderName(text),
                                matchedCardId = matched
                            )
                            status = if (matched != null) {
                                "Card identified."
                            } else {
                                "Number read, but this card isn't in the catalog yet."
                            }
                        }
                        capturing = false
                    }
                    .addOnFailureListener {
                        status = "Couldn't read the card — try again."
                        capturing = false
                    }
                    .addOnCompleteListener { image.close() }
            }

            override fun onError(exception: ImageCaptureException) {
                status = "Camera error — try again."
                capturing = false
            }
        })
    }

    val matchedCard = scanResult?.matchedCardId?.let { id -> cards.firstOrNull { it.id == id } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan card") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasPermission) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                Text(
                    "Camera permission is needed to scan cards.",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(status, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            scanResult?.let { result ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "•••• •••• •••• ${result.last4}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        result.expiry?.let { Text("Expires $it") }
                        result.holderName?.let { Text(it) }
                        matchedCard?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Matched: ${it.name}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack) { Text("Cancel") }
                Button(
                    onClick = { capture() },
                    enabled = hasPermission && !capturing
                ) { Text(if (capturing) "Reading…" else "Capture") }
                if (matchedCard != null && scanResult != null) {
                    Button(onClick = { onIdentified(scanResult!!) }) { Text("View card") }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "The photo never leaves your phone. Only the first 6 and last 4 digits are kept.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
