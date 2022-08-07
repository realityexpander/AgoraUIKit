package com.realityexpander.agorauikit

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.realityexpander.agorauikit.BuildConfig.AGORA_API
import com.realityexpander.agorauikit.BuildConfig.AGORA_TEMP_RTC_TOKEN
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraVideoViewer

@ExperimentalUnsignedTypes
@Composable
fun VideoScreen(
    roomName: String,
    onNavigateUp: () -> Unit = {},
    viewModel: VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var agoraView: AgoraVideoViewer? = null
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { perms ->
                viewModel.onPermissionsResult(
                    acceptedAudioPermission = perms[Manifest.permission.RECORD_AUDIO] == true,
                    acceptedCameraPermission = perms[Manifest.permission.CAMERA] == true,
                )
            }
        )

    // Get permission for audio and camera
    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
            )
        )
    }

    BackHandler {
        agoraView?.leaveChannel()
        onNavigateUp()
    }

    if(viewModel.hasAudioPermission.value && viewModel.hasCameraPermission.value) {
        AndroidView(
            factory = { context ->
                AgoraVideoViewer(
                    context,
                    connectionData = AgoraConnectionData(
                        appId = AGORA_API
                    )
                ).also {
                    it.join(
                        roomName,
                        token = AGORA_TEMP_RTC_TOKEN // only good for 24 hours
                    )
                    agoraView = it
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

}