package com.ivarna.finalbenchmark2.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.SdCard
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ivarna.finalbenchmark2.R

private data class PermissionItem(
        val permission: String,
        val icon: ImageVector,
        val titleRes: Int,
        val descriptionRes: Int
)

private fun buildPermissionList(): List<PermissionItem> {
        val list = mutableListOf<PermissionItem>()
        list.add(
                PermissionItem(
                        Manifest.permission.CAMERA,
                        Icons.Rounded.CameraAlt,
                        R.string.permission_camera_title,
                        R.string.permission_camera_description
                )
        )
        list.add(
                PermissionItem(
                        Manifest.permission.READ_PHONE_STATE,
                        Icons.Rounded.PhoneAndroid,
                        R.string.permission_phone_state_title,
                        R.string.permission_phone_state_description
                )
        )
        list.add(
                PermissionItem(
                        Manifest.permission.BODY_SENSORS,
                        Icons.Rounded.Sensors,
                        R.string.permission_sensors_title,
                        R.string.permission_sensors_description
                )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                list.add(
                        PermissionItem(
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Icons.Rounded.Bluetooth,
                                R.string.permission_bluetooth_title,
                                R.string.permission_bluetooth_description
                        )
                )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                list.add(
                        PermissionItem(
                                Manifest.permission.POST_NOTIFICATIONS,
                                Icons.Rounded.Notifications,
                                R.string.permission_notifications_title,
                                R.string.permission_notifications_description
                        )
                )
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                list.add(
                        PermissionItem(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Icons.Rounded.SdCard,
                                R.string.permission_storage_title,
                                R.string.permission_storage_description
                        )
                )
        }
        return list
}

@Composable
fun PermissionsScreen(
        onNextClicked: () -> Unit,
        onBackClicked: () -> Unit = {},
        modifier: Modifier = Modifier
) {
        val context = LocalContext.current
        val permissionItems = remember { buildPermissionList() }

        val grantedState = remember {
                mutableStateMapOf<String, Boolean>().apply {
                        permissionItems.forEach { item ->
                                put(
                                        item.permission,
                                        ContextCompat.checkSelfPermission(context, item.permission) ==
                                                PackageManager.PERMISSION_GRANTED
                                )
                        }
                }
        }

        val launcher =
                rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                ) { results -> results.forEach { (perm, granted) -> grantedState[perm] = granted } }

        val allGranted = permissionItems.all { grantedState[it.permission] == true }

        Box(
                modifier =
                        modifier.fillMaxSize().background(
                                Brush.verticalGradient(
                                        colors =
                                                listOf(
                                                        MaterialTheme.colorScheme.surface,
                                                        MaterialTheme.colorScheme.primaryContainer
                                                                .copy(alpha = 0.15f)
                                                )
                                )
                        )
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                        Column(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                com.ivarna.finalbenchmark2.ui.components.GlassCard(
                                        modifier = Modifier.size(100.dp),
                                        shape = RoundedCornerShape(50.dp),
                                        containerColor =
                                                MaterialTheme.colorScheme.primaryContainer.copy(
                                                        alpha = 0.6f
                                                ),
                                        borderColor =
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                ) {
                                        Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Rounded.Shield,
                                                        contentDescription =
                                                                stringResource(R.string.permission_required),
                                                        modifier = Modifier.size(48.dp),
                                                        tint = MaterialTheme.colorScheme.primary
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                        text = stringResource(R.string.permission_required_for_data),
                                        style =
                                                MaterialTheme.typography.headlineLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        permissionItems.forEach { item ->
                                                val granted = grantedState[item.permission] == true
                                                com.ivarna.finalbenchmark2.ui.components.GlassCard(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(18.dp)
                                                ) {
                                                        Row(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .padding(16.dp),
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Icon(
                                                                        imageVector = item.icon,
                                                                        contentDescription = null,
                                                                        tint =
                                                                                if (granted)
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                                else
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant,
                                                                        modifier = Modifier.size(28.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(16.dp))
                                                                Column(modifier = Modifier.weight(1f)) {
                                                                        Text(
                                                                                text =
                                                                                        stringResource(
                                                                                                item.titleRes
                                                                                        ),
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .titleSmall
                                                                                                .copy(
                                                                                                        fontWeight =
                                                                                                                FontWeight
                                                                                                                        .SemiBold
                                                                                                ),
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurface
                                                                        )
                                                                        Text(
                                                                                text =
                                                                                        stringResource(
                                                                                                item.descriptionRes
                                                                                        ),
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                        )
                                                                }
                                                                Spacer(modifier = Modifier.width(8.dp))
                                                                if (granted) {
                                                                        Icon(
                                                                                imageVector =
                                                                                        Icons.Rounded
                                                                                                .CheckCircle,
                                                                                contentDescription =
                                                                                        stringResource(
                                                                                                R.string
                                                                                                        .supported
                                                                                        ),
                                                                                tint =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary,
                                                                                modifier =
                                                                                        Modifier.size(24.dp)
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }

                        Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                if (!allGranted) {
                                        Button(
                                                onClick = {
                                                        launcher.launch(
                                                                permissionItems
                                                                        .map { it.permission }
                                                                        .toTypedArray()
                                                        )
                                                },
                                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        ),
                                                shape = RoundedCornerShape(16.dp)
                                        ) {
                                                Text(
                                                        text = stringResource(R.string.grant_access),
                                                        style =
                                                                MaterialTheme.typography.titleMedium
                                                                        .copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .SemiBold
                                                                        )
                                                )
                                        }
                                }

                                Button(
                                        onClick = { onNextClicked() },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        colors =
                                                if (allGranted)
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                                else ButtonDefaults.outlinedButtonColors(),
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        Text(
                                                text = stringResource(R.string.next_step),
                                                style =
                                                        MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.SemiBold
                                                        )
                                        )
                                }
                        }
                }
        }
}
