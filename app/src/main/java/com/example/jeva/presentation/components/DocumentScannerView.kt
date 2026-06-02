package com.example.jeva.presentation.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DocumentScannerView(
    onFlowCompleted: (Uri, Uri) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (!cameraPermissionState.status.isGranted) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Verificación de Identidad (KYC)", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Requerimos escanear tu documento por ambas caras.", color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Conceder Permiso")
            }
        }
    } else {
        CameraSequenceLayer(context, onFlowCompleted)
    }
}

@Composable
fun CameraSequenceLayer(context: Context, onFlowCompleted: (Uri, Uri) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Mantenemos la instancia de ImageCapture estable
    val imageCapture = remember { ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build() }

    // Estados del flujo
    var currentStep by remember { mutableStateOf(1) } // 1 = Frontal, 2 = Reverso
    var frontUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx -> PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
                    } catch (e: Exception) {
                        Log.e("JevaCam", "Error al vincular cámara", e)
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // Recuadro guía dinámico (Cambia de color según el paso para dar feedback visual)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.28f)
                .align(Alignment.Center)
                .border(BorderStroke(3.dp, if (currentStep == 1) Color.Cyan else Color.Green), shape = RoundedCornerShape(12.dp))
        )

        // Indicador de texto en pantalla
        Card(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
        ) {
            Text(
                text = if (currentStep == 1) "PASO 1: FOTO FRONTAL DE LA CÉDULA" else "PASO 2: FOTO REVERSO DE LA CÉDULA",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Botón de disparo / Obturador
        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp).align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = {
                        isSaving = true
                        val fileName = "doc_${if (currentStep == 1) "front" else "back"}_${System.currentTimeMillis()}.jpg"
                        val photoFile = File(context.cacheDir, fileName)
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        // FORZAMOS EL USO DEL EXECUTOR PRINCIPAL PARA EVITAR BLOQUEOS EN EMULADORES
                        val mainExecutor = ContextCompat.getMainExecutor(context)

                        imageCapture.takePicture(
                            outputOptions,
                            mainExecutor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    val savedUri = Uri.fromFile(photoFile)
                                    Log.d("JevaCam", "¡Foto guardada con éxito! -> $savedUri")

                                    // IMPORTANTE: Aseguramos que el cambio de estado corra en el hilo correcto
                                    isSaving = false
                                    if (currentStep == 1) {
                                        frontUri = savedUri
                                        currentStep = 2 // Cambia dinámicamente al paso 2
                                    } else {
                                        frontUri?.let { onFlowCompleted(it, savedUri) }
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    isSaving = false
                                    Log.e("JevaCam", "Error en la captura: ${exception.message}", exception)
                                }
                            }
                        )
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.size(72.dp),
                    border = BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
                ) {}
            }
        }
    }
}