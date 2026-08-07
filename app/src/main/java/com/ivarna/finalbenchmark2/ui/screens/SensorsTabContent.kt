package com.ivarna.finalbenchmark2.ui.screens

import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivarna.finalbenchmark2.R
import com.ivarna.finalbenchmark2.ui.components.BodySensorsPermissionRequest
import com.ivarna.finalbenchmark2.ui.viewmodels.SensorViewModel
import com.ivarna.finalbenchmark2.utils.SensorUtils
import com.ivarna.finalbenchmark2.ui.components.GlassCard
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun SensorsTabContent(sensorViewModel: SensorViewModel = viewModel()) {
    val context = LocalContext.current
    val sensorStates by sensorViewModel.sensorStates.collectAsState()
    val supportedCount by sensorViewModel.supportedSensorCount.collectAsState()
    val totalCount by sensorViewModel.totalSensorCount.collectAsState()
    
    // Initialize the ViewModel with context
    LaunchedEffect(context) {
        sensorViewModel.initialize(context)
    }
    
    // Clean up when the composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            sensorViewModel.unregisterAllListeners()
        }
    }
    
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.sensors_information),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            
            // Sensor Capabilities Summary Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sensor_capabilities),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Progress bar showing supported sensors
                    val progress = if (totalCount > 0) supportedCount.toFloat() / totalCount.toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Supported: $supportedCount / Total: $totalCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Sensor List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(sensorStates) { sensorState ->
                    SensorCard(sensorState)
                }
            }
        }
}

@Composable
fun SensorCard(sensorState: com.ivarna.finalbenchmark2.ui.viewmodels.SensorUiState) {
    // Check if this is a heart rate sensor that requires permission
    if (sensorState.type == android.hardware.Sensor.TYPE_HEART_RATE) {
        // Show permission request if needed
        BodySensorsPermissionRequest {
            HeartRateSensorCard(sensorState)
        }
    } else {
        // Standard sensors don't need permission - show directly
        StandardSensorCard(sensorState)
    }
}

@Composable
fun StandardSensorCard(sensorState: com.ivarna.finalbenchmark2.ui.viewmodels.SensorUiState) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sensorState.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Status Badge
                if (sensorState.isSupported) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.ivarna.finalbenchmark2.R.drawable.check_24),
                            contentDescription = stringResource(R.string.supported),
                            modifier = Modifier
                                .size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.active),
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.ivarna.finalbenchmark2.R.drawable.close_24),
                            contentDescription = stringResource(R.string.not_supported),
                            modifier = Modifier
                                .size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.not_available),
                            fontSize = 12.sp,
                            color = Color(0xFFEF5350),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (sensorState.isSupported) {
                // Vendor and Power info
                Text(
                    text = "Vendor: ${sensorState.vendor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Power: ${sensorState.power} mA",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Live sensor data
                if (sensorState.values.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.live_data),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = SensorUtils.formatSensorValues(sensorState.type, sensorState.values),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.waiting_for_sensor_data),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(8.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.this_sensor_is_not_available_on),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HeartRateSensorCard(sensorState: com.ivarna.finalbenchmark2.ui.viewmodels.SensorUiState) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sensorState.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Status Badge - show permission status for heart rate sensor
                if (sensorState.isSupported) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.ivarna.finalbenchmark2.R.drawable.check_24),
                            contentDescription = stringResource(R.string.supported),
                            modifier = Modifier
                                .size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.supported),
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.ivarna.finalbenchmark2.R.drawable.close_24),
                            contentDescription = stringResource(R.string.not_supported),
                            modifier = Modifier
                                .size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.not_available),
                            fontSize = 12.sp,
                            color = Color(0xFFEF5350),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (sensorState.isSupported) {
                // Vendor and Power info
                Text(
                    text = "Vendor: ${sensorState.vendor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Power: ${sensorState.power} mA",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Check permission for heart rate data
                val context = LocalContext.current
                val hasPermission = android.content.pm.PackageManager.PERMISSION_GRANTED ==
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.BODY_SENSORS
                    )
                
                if (hasPermission) {
                    // Show live heart rate data if permission granted
                    if (sensorState.values.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.live_data),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = SensorUtils.formatSensorValues(sensorState.type, sensorState.values),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.waiting_for_sensor_data),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(8.dp)
                        )
                    }
                } else {
                    // Show permission required message if no permission
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.permission_required_for_data),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(8.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.this_sensor_is_not_available_on),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}