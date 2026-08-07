package com.ivarna.finalbenchmark2.ui.screens
import com.ivarna.finalbenchmark2.R

import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.ivarna.finalbenchmark2.ui.components.CameraPermissionRequest
import com.ivarna.finalbenchmark2.ui.components.InformationRow
import com.ivarna.finalbenchmark2.ui.components.PhoneStatePermissionRequest
import com.ivarna.finalbenchmark2.ui.viewmodels.HardwareViewModel
import com.ivarna.finalbenchmark2.ui.components.GlassCard
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HardwareTabContent(viewModel: HardwareViewModel) {
        var batterySpecs by remember {
                mutableStateOf<com.ivarna.finalbenchmark2.utils.BatterySpec?>(null)
        }
        var networkSpecs by remember {
                mutableStateOf<com.ivarna.finalbenchmark2.utils.NetworkSpec?>(null)
        }
        var cameraSpecs by remember {
                mutableStateOf<List<com.ivarna.finalbenchmark2.utils.CameraSpec>?>(null)
        }
        var memoryStorageSpecs by remember {
                mutableStateOf<com.ivarna.finalbenchmark2.utils.MemoryStorageSpec?>(null)
        }
        var audioMediaSpecs by remember {
                mutableStateOf<com.ivarna.finalbenchmark2.utils.AudioMediaSpec?>(null)
        }
        var peripheralsSpecs by remember {
                mutableStateOf<com.ivarna.finalbenchmark2.utils.PeripheralsSpec?>(null)
        }
        var isLoading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }

        // Observe LiveData and update state variables
        DisposableEffect(viewModel) {
                val batteryObserver =
                        Observer<com.ivarna.finalbenchmark2.utils.BatterySpec?> { newValue ->
                                batterySpecs = newValue
                        }
                val networkObserver =
                        Observer<com.ivarna.finalbenchmark2.utils.NetworkSpec?> { newValue ->
                                networkSpecs = newValue
                        }
                val cameraObserver =
                        Observer<List<com.ivarna.finalbenchmark2.utils.CameraSpec>?> { newValue ->
                                cameraSpecs = newValue
                        }
                val memoryStorageObserver =
                        Observer<com.ivarna.finalbenchmark2.utils.MemoryStorageSpec?> { newValue ->
                                memoryStorageSpecs = newValue
                        }
                val audioMediaObserver =
                        Observer<com.ivarna.finalbenchmark2.utils.AudioMediaSpec?> { newValue ->
                                audioMediaSpecs = newValue
                        }
                val peripheralsObserver =
                        Observer<com.ivarna.finalbenchmark2.utils.PeripheralsSpec?> { newValue ->
                                peripheralsSpecs = newValue
                        }
                val loadingObserver = Observer<Boolean> { newValue -> isLoading = newValue }
                val errorObserver = Observer<String?> { newValue -> error = newValue }

                viewModel.batterySpecs.observeForever(batteryObserver)
                viewModel.networkSpecs.observeForever(networkObserver)
                viewModel.cameraSpecs.observeForever(cameraObserver)
                viewModel.memoryStorageSpecs.observeForever(memoryStorageObserver)
                viewModel.audioMediaSpecs.observeForever(audioMediaObserver)
                viewModel.peripheralsSpecs.observeForever(peripheralsObserver)
                viewModel.isLoading.observeForever(loadingObserver)
                viewModel.error.observeForever(errorObserver)

                onDispose {
                        viewModel.batterySpecs.removeObserver(batteryObserver)
                        viewModel.networkSpecs.removeObserver(networkObserver)
                        viewModel.cameraSpecs.removeObserver(cameraObserver)
                        viewModel.memoryStorageSpecs.removeObserver(memoryStorageObserver)
                        viewModel.audioMediaSpecs.removeObserver(audioMediaObserver)
                        viewModel.peripheralsSpecs.removeObserver(peripheralsObserver)
                        viewModel.isLoading.removeObserver(loadingObserver)
                        viewModel.error.removeObserver(errorObserver)
                }
        }

        LaunchedEffect(Unit) { viewModel.loadHardwareSpecs() }

        if (isLoading) {
                Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                ) { CircularProgressIndicator() }
        } else {
                LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        item { BatteryCard(batterySpecs) }
                        item { PhoneStatePermissionRequest { ConnectivityCard(networkSpecs) } }
                        item { CameraPermissionRequest { CameraCard(cameraSpecs) } }
                        item { MemoryStorageCard(memoryStorageSpecs) }
                        item { AudioMediaCard(audioMediaSpecs) }
                        item { PeripheralsCard(peripheralsSpecs) }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryCard(batterySpecs: com.ivarna.finalbenchmark2.utils.BatterySpec?) {
        if (batterySpecs != null) {
                GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Rounded.BatteryFull,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = stringResource(R.string.battery_information),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(stringResource(R.string.level), "${batterySpecs.level}%"),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(stringResource(R.string.status_2), batterySpecs.status),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.technology),
                                                                batterySpecs.technology
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.temperature),
                                                                "${batterySpecs.temperature}°C"
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.voltage),
                                                                "${batterySpecs.voltage}V"
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(stringResource(R.string.health), batterySpecs.health),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.design_capacity),
                                                                batterySpecs.designCapacity
                                                        ),
                                        isLastItem = false
                                )
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectivityCard(networkSpecs: com.ivarna.finalbenchmark2.utils.NetworkSpec?) {
        if (networkSpecs != null) {
                GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Rounded.NetworkCell,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = stringResource(R.string.connectivity),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.network_type),
                                                                networkSpecs.networkType
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.signal_strength),
                                                                networkSpecs.signalStrength
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(stringResource(R.string.wifi_speed), networkSpecs.wifiSpeed),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.wifi_frequency),
                                                                networkSpecs.wifiFrequency
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.wifi_standard),
                                                                networkSpecs.wifiStandard
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.bluetooth_features),
                                                                networkSpecs.bluetoothFeatures
                                                                        .joinToString(", ")
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.nfc_supported),
                                                                if (networkSpecs.nfcSupported) "Yes"
                                                                else "No"
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.ir_blaster),
                                                                if (networkSpecs.irBlasterSupported)
                                                                        "Supported"
                                                                else "Not Supported"
                                                        ),
                                        isLastItem = false
                                )
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCard(cameraSpecs: List<com.ivarna.finalbenchmark2.utils.CameraSpec>?) {
        if (cameraSpecs != null && cameraSpecs.isNotEmpty()) {
                GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Rounded.CameraAlt,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = stringResource(R.string.camera_modules),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                cameraSpecs.forEachIndexed { index, camera ->
                                        Column(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(vertical = 4.dp)
                                        ) {
                                                Text(
                                                        text =
                                                                "Camera ${camera.id} (${camera.direction})",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineSmall,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.padding(bottom = 4.dp)
                                                )
                                                InformationRow(
                                                        itemValue =
                                                                com.ivarna.finalbenchmark2.domain
                                                                        .model.ItemValue.Text(
                                                                        stringResource(R.string.resolution),
                                                                        camera.resolution
                                                                ),
                                                        isLastItem = false
                                                )
                                                InformationRow(
                                                        itemValue =
                                                                com.ivarna.finalbenchmark2.domain
                                                                        .model.ItemValue.Text(
                                                                        stringResource(R.string.aperture),
                                                                        camera.aperture
                                                                ),
                                                        isLastItem = false
                                                )
                                                InformationRow(
                                                        itemValue =
                                                                com.ivarna.finalbenchmark2.domain
                                                                        .model.ItemValue.Text(
                                                                        stringResource(R.string.focal_length),
                                                                        camera.focalLength
                                                                ),
                                                        isLastItem = false
                                                )
                                                InformationRow(
                                                        itemValue =
                                                                com.ivarna.finalbenchmark2.domain
                                                                        .model.ItemValue.Text(
                                                                        stringResource(R.string.capabilities),
                                                                        camera.capabilities
                                                                                .joinToString(", ")
                                                                ),
                                                        isLastItem = index == cameraSpecs.size - 1
                                                )
                                        }
                                }
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryStorageCard(memoryStorageSpecs: com.ivarna.finalbenchmark2.utils.MemoryStorageSpec?) {
        if (memoryStorageSpecs != null) {
                GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Rounded.Storage,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = stringResource(R.string.memory_storage),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.total_ram),
                                                                memoryStorageSpecs.ramTotal
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.available_ram),
                                                                memoryStorageSpecs.ramAvailable
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.total_storage),
                                                                memoryStorageSpecs.storageTotal
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.available_storage),
                                                                memoryStorageSpecs.storageAvailable
                                                        ),
                                        isLastItem = false
                                )
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioMediaCard(audioMediaSpecs: com.ivarna.finalbenchmark2.utils.AudioMediaSpec?) {
        if (audioMediaSpecs != null) {
                GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Rounded.Headphones,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = stringResource(R.string.audio_media),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.speakers),
                                                                audioMediaSpecs.speakers
                                                                        .joinToString(", ")
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.widevine_level),
                                                                audioMediaSpecs.widevineLevel
                                                        ),
                                        isLastItem = false
                                )

                                // Supported codecs - show in a scrollable text area
                                Text(
                                        text = stringResource(R.string.supported_codecs),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                                Text(
                                        text = audioMediaSpecs.supportedCodecs.joinToString(", "),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeripheralsCard(peripheralsSpecs: com.ivarna.finalbenchmark2.utils.PeripheralsSpec?) {
        if (peripheralsSpecs != null) {
                GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Rounded.Devices,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = stringResource(R.string.peripherals),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.biometric_support),
                                                                peripheralsSpecs.biometricSupport
                                                                        .joinToString(", ")
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.sim_slots),
                                                                "${peripheralsSpecs.simSlots}"
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.vibration_amplitude_control),
                                                                if (peripheralsSpecs
                                                                                .vibrationSupport
                                                                )
                                                                        "Supported"
                                                                else "Not Supported"
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.usb_otg),
                                                                if (peripheralsSpecs.usbOtg)
                                                                        "Supported"
                                                                else "Not Supported"
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.display_hdr),
                                                                peripheralsSpecs.displayHdr
                                                                        .joinToString(", ")
                                                        ),
                                        isLastItem = false
                                )
                                InformationRow(
                                        itemValue =
                                                com.ivarna.finalbenchmark2.domain.model.ItemValue
                                                        .Text(
                                                                stringResource(R.string.system_architecture),
                                                                peripheralsSpecs.systemArchitecture
                                                                        .joinToString(", ")
                                                        ),
                                        isLastItem = true
                                )
                        }
                }
        }
}
