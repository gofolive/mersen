package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.DiningHall
import com.example.data.model.VerificationStatus
import com.example.ui.components.AntiPassbackAlertModal
import com.example.ui.model.CashierTab
import com.example.ui.viewmodel.MealCardViewModel

@Composable
fun CashierScannerScreen(
    viewModel: MealCardViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gatekeeperHall by viewModel.gatekeeperHall.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()
    val manualIdInput by viewModel.manualIdInput.collectAsState()
    val verificationResult by viewModel.verificationResult.collectAsState()
    val scanLogs by viewModel.scanLogsList.collectAsState()
    val currentTab by viewModel.cashierTab.collectAsState()

    var showManualInput by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Camera ready for QR scanner", Toast.LENGTH_SHORT).show()
        }
    }

    // Shift Statistics
    val shiftLogs = scanLogs.filter { it.mealSession == activeSession }
    val totalScanned = shiftLogs.size
    val grantedCount = shiftLogs.count { it.status == VerificationStatus.GRANTED }
    val passbackViolations = shiftLogs.count { it.status == VerificationStatus.REJECTED_PASSBACK }
    val ineligibles = shiftLogs.count { !it.status.isApproved && it.status != VerificationStatus.REJECTED_PASSBACK }

    // Laser Animation
    val infiniteTransition = rememberInfiniteTransition(label = "cashier_scanner_laser")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    // Verification Result Modal Dialog
    AntiPassbackAlertModal(
        result = verificationResult,
        onDismiss = { viewModel.dismissVerificationModal() }
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Station and Hall Selector Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TURNSTILE STATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${gatekeeperHall.code} • Turnstile Gate 1",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF004D40)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DiningHall.values().forEach { hall ->
                            FilterChip(
                                selected = gatekeeperHall == hall,
                                onClick = { viewModel.setGatekeeperHall(hall) },
                                label = { Text(hall.code, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00796B),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // TAB 1: SCANNER VIEWPORT & TESTING
        if (currentTab == CashierTab.SCANNER) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1614)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cashier_scanner_box")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Scanner Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SCANNER ACTIVE • ${activeSession.sessionName.uppercase()}",
                                    color = Color(0xFF80CBC4),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }

                            if (unsyncedCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFD84315),
                                    modifier = Modifier.clickable { viewModel.syncOfflineRecords() }
                                ) {
                                    Text(
                                        text = "$unsyncedCount Cached (Sync)",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive Viewfinder Viewport
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF13221F))
                                .border(2.dp, Color(0xFF00BFA5).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                                .clickable {
                                    // Clicking directly on viewfinder scans current student
                                    viewModel.processScan("MTU/14022/13")
                                }
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeW = 4f
                                val cornerLen = 28f
                                val color = Color(0xFF00E676)

                                // Reticle corners
                                drawLine(color, Offset(24f, 24f), Offset(24f + cornerLen, 24f), strokeW)
                                drawLine(color, Offset(24f, 24f), Offset(24f, 24f + cornerLen), strokeW)
                                drawLine(color, Offset(size.width - 24f, 24f), Offset(size.width - 24f - cornerLen, 24f), strokeW)
                                drawLine(color, Offset(size.width - 24f, 24f), Offset(size.width - 24f, 24f + cornerLen), strokeW)
                                drawLine(color, Offset(24f, size.height - 24f), Offset(24f + cornerLen, size.height - 24f), strokeW)
                                drawLine(color, Offset(24f, size.height - 24f), Offset(24f, size.height - 24f - cornerLen), strokeW)
                                drawLine(color, Offset(size.width - 24f, size.height - 24f), Offset(size.width - 24f - cornerLen, size.height - 24f), strokeW)
                                drawLine(color, Offset(size.width - 24f, size.height - 24f), Offset(size.width - 24f, size.height - 24f - cornerLen), strokeW)

                                // Animated Laser Line
                                val currentLaserY = size.height * laserYRatio
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0x3300E676), Color(0xCC00E676), Color.Transparent),
                                        startY = currentLaserY - 18f,
                                        endY = currentLaserY + 18f
                                    ),
                                    topLeft = Offset(0f, currentLaserY - 18f),
                                    size = Size(size.width, 36f)
                                )
                                drawLine(
                                    color = Color(0xFF00E676),
                                    start = Offset(20f, currentLaserY),
                                    end = Offset(size.width - 20f, currentLaserY),
                                    strokeWidth = 3f
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = Color(0xFF80CBC4),
                                    modifier = Modifier.size(46.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ready to Scan Student QR Pass",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Anti-passback engine actively running",
                                    color = Color(0xFF80CBC4),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Primary Scan Button
                        Button(
                            onClick = {
                                if (!hasCameraPermission) {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                                viewModel.processScan("MTU/14022/13")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("primary_scan_button")
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SCAN STUDENT QR CODE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Rapid Anti-Passback Test Controls
                        Text(
                            text = "ANTI-PASSBACK VERIFICATION TRIGGERS:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { viewModel.processScan("MTU/14022/13") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("scan_dejene_granted")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Scan Dejene", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("First Access (Granted)", fontSize = 8.sp, color = Color(0xFF80CBC4))
                                }
                            }

                            Button(
                                onClick = { viewModel.processScan("MTU/14022/13") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("scan_dejene_passback")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("⚡ Rescan Dejene", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Duplicate (Passback Alert)", fontSize = 8.sp, color = Color(0xFFFFCDD2))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.processScan("MTU/13890/13") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF80CBC4)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Scan Fasting", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.processScan("MTU/12099/12") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8A80)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Scan Suspended", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Manual Student ID Input
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showManualInput = !showManualInput }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Keyboard, contentDescription = null, tint = Color(0xFF80CBC4), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showManualInput) "Hide ID Keypad" else "Manual Student ID Entry",
                                fontSize = 11.sp,
                                color = Color(0xFF80CBC4),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (showManualInput) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = manualIdInput,
                                    onValueChange = { viewModel.setManualIdInput(it) },
                                    placeholder = { Text("e.g. MTU/14022/13", fontSize = 11.sp, color = Color.Gray) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        if (manualIdInput.isNotBlank()) viewModel.processScan(manualIdInput)
                                    }),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        if (manualIdInput.isNotBlank()) viewModel.processScan(manualIdInput)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(50.dp)
                                ) {
                                    Text("VERIFY", color = Color(0xFF00382F), fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // SHIFT STATISTICS & AUDIT LOGS
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Cashier Shift Overview (${activeSession.sessionName})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("SERVED", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("$grantedCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF004D40))
                                Text("Students", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("PASSBACK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                Text("$passbackViolations", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFC62828))
                                Text("Blocked", fontSize = 9.sp, color = Color(0xFFB71C1C))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("DENIED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                Text("$ineligibles", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                                Text("Ineligible", fontSize = 9.sp, color = Color(0xFFBF360C))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Live Turnstile Stream
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Turnstile Entrance Audit Stream", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "$totalScanned Scans", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(shiftLogs.take(8)) { log ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (log.status.isApproved) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                        ) {
                            Icon(
                                imageVector = if (log.status.isApproved) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (log.status.isApproved) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(text = log.studentName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "${log.mtuId} • ${log.mealSession.sessionName}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (!log.status.isApproved && log.rejectionReason != null) {
                                Text(text = log.rejectionReason, fontSize = 9.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Text(text = log.formattedTime(), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
