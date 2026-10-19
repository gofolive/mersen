package com.example.ui.components

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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealSession
import com.example.ui.model.AuthenticatedUser
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
    currentUser: AuthenticatedUser?,
    activeSession: MealSession,
    isOfflineMode: Boolean,
    onToggleOfflineMode: () -> Unit,
    unsyncedCount: Int,
    onTriggerSync: () -> Unit,
    onLogout: () -> Unit
) {
    val actor = currentUser?.actor ?: UserActor.STUDENT

    val headerBg = when (actor) {
        UserActor.STUDENT -> Color(0xFF004D40)
        UserActor.CASHIER -> Color(0xFF142421)
        UserActor.ADMIN -> Color(0xFF281E1E)
    }

    val actorAccentColor = when (actor) {
        UserActor.STUDENT -> Color(0xFFFFD54F)
        UserActor.CASHIER -> Color(0xFF00E676)
        UserActor.ADMIN -> Color(0xFFFF8A80)
    }

    Surface(
        color = headerBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Top Row: MTU Crest and Logout Action
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
                        Text(text = actor.iconEmoji, fontSize = 20.sp)
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
                            text = "Tepi Campus • Cafeteria Access Portal",
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Log Out Action Button
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFD32F2F).copy(alpha = 0.85f),
                    modifier = Modifier
                        .clickable { onLogout() }
                        .testTag("logout_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log Out",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Log Out",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prominent Current Actor Strip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.28f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LOGGED IN: ${actor.title.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = actorAccentColor,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${currentUser?.displayName ?: "User"} • ${currentUser?.stationOrDept ?: ""}",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Network status indicator
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isOfflineMode) Color(0xFFD84315) else Color(0xFF00796B),
                        modifier = Modifier.clickable { onToggleOfflineMode() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isOfflineMode) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
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
