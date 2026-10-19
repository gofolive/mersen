package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealSession
import com.example.data.model.Student
import com.example.ui.model.UserActor

// Legacy compatibility
enum class AppRole(val title: String, val icon: String) {
    STUDENT("Student Card", "🎓"),
    GATEKEEPER("Scanner Gate", "🛡️"),
    ADMIN("Admin Mobile", "📊"),
    WEB_PORTAL("Web Dashboard", "🌐")
}

@Composable
fun MtuHeader(
    currentActor: UserActor,
    onSelectActor: (UserActor) -> Unit,
    currentStudent: Student?,
    activeSession: MealSession,
    isOfflineMode: Boolean,
    onToggleOfflineMode: () -> Unit,
    unsyncedCount: Int,
    onTriggerSync: () -> Unit
) {
    var showActorDialog by remember { mutableStateOf(false) }

    if (showActorDialog) {
        ActorSelectionModal(
            currentActor = currentActor,
            onSelectActor = { onSelectActor(it) },
            onDismiss = { showActorDialog = false }
        )
    }

    val actorBgGradient = when (currentActor) {
        UserActor.STUDENT -> Color(0xFF004D40)
        UserActor.CASHIER -> Color(0xFF1B2E2B)
        UserActor.ADMIN -> Color(0xFF281E1E)
    }

    val actorAccentColor = when (currentActor) {
        UserActor.STUDENT -> Color(0xFFFFD54F)
        UserActor.CASHIER -> Color(0xFF00E676)
        UserActor.ADMIN -> Color(0xFFFF8A80)
    }

    Surface(
        color = actorBgGradient,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Institutional Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Text(text = currentActor.iconEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "MIZAN-TEPI UNIVERSITY",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Tepi Campus • Cafeteria Access System",
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Switch Actor Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .clickable { showActorDialog = true }
                        .testTag("switch_actor_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Actor",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Switch Role",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prominent Current Actor Badge Strip
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LOGGED IN AS: ${currentActor.title.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = actorAccentColor,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = when (currentActor) {
                                UserActor.STUDENT -> "${currentStudent?.fullName ?: "Student"} (${currentStudent?.mtuId ?: "MTU/14022/13"}) • Info Systems"
                                UserActor.CASHIER -> "Officer Haile M. • Entrance Turnstile Gate 1 (Main Hall)"
                                UserActor.ADMIN -> "Cafeteria Operations Manager • Full Access"
                            },
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Network Offline status
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isOfflineMode) Color(0xFFD84315) else Color(0xFF00796B),
                        modifier = Modifier
                            .clickable { onToggleOfflineMode() }
                            .testTag("network_mode_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isOfflineMode) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isOfflineMode) "OFFLINE" else "ONLINE",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
