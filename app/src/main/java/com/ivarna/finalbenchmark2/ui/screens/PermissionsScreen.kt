package com.ivarna.finalbenchmark2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ivarna.finalbenchmark2.utils.OnboardingPreferences
import com.ivarna.finalbenchmark2.utils.RootAccessManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Brush

sealed interface RootUiState {
        object Checking : RootUiState
        object Granted : RootUiState
        object NotAvailable : RootUiState
}

@Composable
fun RootCheckScreen(
        onNextClicked: () -> Unit,
        onBackClicked: () -> Unit = {},
        onRetryRootAccess: () -> Unit = {},
        modifier: Modifier = Modifier
) {
        var rootUiState by remember { mutableStateOf<RootUiState>(RootUiState.Checking) }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val onboardingPreferences = remember { OnboardingPreferences(context) }

        // Perform automatic root check when screen loads
        val hasRootChecked = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
                if (!hasRootChecked.value) {
                        scope.launch {
                                // Use RootAccessManager.isRootGranted() - this will use cached
                                // result if available
                                val hasRoot = RootAccessManager.isRootGranted()
                                rootUiState =
                                        if (hasRoot) {
                                                RootUiState.Granted
                                        } else {
                                                RootUiState.NotAvailable
                                        }
                                hasRootChecked.value = true
                        }
                }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        )
                    )
                )
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                        // Central content
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                        ) {
                                // Icon Container with status color
                                val iconColor =
                                        when (rootUiState) {
                                                is RootUiState.Checking ->
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                is RootUiState.Granted ->
                                                        MaterialTheme.colorScheme.primary
                                                is RootUiState.NotAvailable ->
                                                        MaterialTheme.colorScheme.error
                                        }

                                com.ivarna.finalbenchmark2.ui.components.GlassCard(
                                        modifier = Modifier.size(100.dp),
                                        shape = RoundedCornerShape(50.dp),
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                ) {
                                        Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Rounded.Security,
                                                        contentDescription = "Root 权限状态",
                                                        modifier = Modifier.size(48.dp),
                                                        tint = iconColor
                                                )
                                        }
                                }

                                // Separate loading indicator
                                if (rootUiState is RootUiState.Checking) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                color = MaterialTheme.colorScheme.primary
                                        )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Headline
                                val headlineText =
                                        when (rootUiState) {
                                                is RootUiState.Checking -> "正在检测 Root 权限…"
                                                is RootUiState.Granted -> "已获得 Root 权限"
                                                is RootUiState.NotAvailable ->
                                                        "未检测到 Root 权限"
                                        }

                                val headlineColor =
                                        when (rootUiState) {
                                                is RootUiState.Checking ->
                                                        MaterialTheme.colorScheme.onSurface
                                                is RootUiState.Granted ->
                                                        MaterialTheme.colorScheme.primary
                                                is RootUiState.NotAvailable ->
                                                        MaterialTheme.colorScheme.error
                                        }

                                Text(
                                        text = headlineText,
                                        style =
                                                MaterialTheme.typography.headlineLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = headlineColor,
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Status Information Card
                                com.ivarna.finalbenchmark2.ui.components.GlassCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp)
                                ) {
                                        Column(modifier = Modifier.padding(20.dp)) {
                                                val statusText =
                                                        when (rootUiState) {
                                                                is RootUiState.Checking ->
                                                                        "正在检测设备的 Root 权限…"
                                                                is RootUiState.Granted ->
                                                                        "已启用完整控制权限。你可以使用CPU核心绑定、GPU频率监控、调速器调节等高级功能。"
                                                                is RootUiState.NotAvailable ->
                                                                        "以标准模式运行。跑分功能正常可用，但详细硬件数据（GPU负载、精确频率）和相关优化将被禁用。"
                                                        }

                                                Text(
                                                        text = statusText,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        textAlign = TextAlign.Start
                                                )
                                        }
                                }

                                // Root Warning Card - shown when root is granted
                                if (rootUiState is RootUiState.Granted) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        com.ivarna.finalbenchmark2.ui.components.GlassCard(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(20.dp),
                                                containerColor = Color(0xFFFF9800).copy(alpha = 0.1f),
                                                borderColor = Color(0xFFFF9800).copy(alpha = 0.5f)
                                        ) {
                                                Column(modifier = Modifier.padding(20.dp)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Icon(
                                                                        imageVector = Icons.Rounded.Security,
                                                                        contentDescription = "警告",
                                                                        tint = MaterialTheme.colorScheme.error,
                                                                        modifier = Modifier.size(24.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(12.dp))
                                                                Text(
                                                                        text = "⚠️ Root 权限警告",
                                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                                                fontWeight = FontWeight.Bold
                                                                        ),
                                                                        color = MaterialTheme.colorScheme.error
                                                                )
                                                        }

                                                        Spacer(modifier = Modifier.height(12.dp))

                                                        Text(
                                                                text = "在Root权限下运行跑分会将设备推向极限。设备发热可能明显高于正常水平，存在造成硬件损坏的风险。",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                color = MaterialTheme.colorScheme.onSurface,
                                                                textAlign = TextAlign.Start
                                                        )

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        Text(
                                                                text = "⚠️ 对于在启用Root权限跑分期间或之后设备可能出现的任何损坏，我们概不负责。",
                                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                                color = Color(0xFFFF6B00),
                                                                textAlign = TextAlign.Start
                                                        )
                                                }
                                        }
                                }

                                // "Request Root Again" button for manual retry
                                if (rootUiState is RootUiState.NotAvailable) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedButton(
                                                onClick = {
                                                        rootUiState = RootUiState.Checking
                                                        // Use forceRefresh() to clear cache and
                                                        // perform new check
                                                        scope.launch {
                                                                delay(
                                                                        500
                                                                ) // Small delay for better UX
                                                                val hasRoot =
                                                                        RootAccessManager
                                                                                .forceRefresh()
                                                                rootUiState =
                                                                        if (hasRoot) {
                                                                                RootUiState.Granted
                                                                        } else {
                                                                                RootUiState
                                                                                        .NotAvailable
                                                                        }
                                                        }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                        ) {
                                                Text(
                                                        text = "重新申请 Root 权限",
                                                        style = MaterialTheme.typography.titleMedium
                                                )
                                        }
                                }
                        }

                        // Action Button
                        Button(
                                onClick = { onNextClicked() },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                shape = RoundedCornerShape(16.dp)
                        ) {
                                Text(
                                        text = "下一步",
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                )
                                )
                        }
                }
        }
}



