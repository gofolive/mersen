package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.UserActor

@Composable
fun LoginScreen(
    onLoginSuccess: (actor: UserActor, username: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedActor by remember { mutableStateOf(UserActor.STUDENT) }
    var username by remember { mutableStateOf("MTU/14022/13") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun performLogin(actor: UserActor, u: String, p: String) {
        val cleanUser = u.trim()
        val cleanPass = p.trim()

        if (cleanUser.isBlank()) {
            errorMessage = "Please enter your username or ID"
            return
        }

        when (actor) {
            UserActor.STUDENT -> {
                // Accepts MTU ID or 'dejene'
                if (cleanPass.isNotEmpty()) {
                    errorMessage = null
                    onLoginSuccess(UserActor.STUDENT, cleanUser)
                } else {
                    errorMessage = "Please enter password"
                }
            }
            UserActor.CASHIER -> {
                if (cleanPass.isNotEmpty()) {
                    errorMessage = null
                    onLoginSuccess(UserActor.CASHIER, cleanUser)
                } else {
                    errorMessage = "Please enter cashier security code"
                }
            }
            UserActor.ADMIN -> {
                if (cleanPass.isNotEmpty()) {
                    errorMessage = null
                    onLoginSuccess(UserActor.ADMIN, cleanUser)
                } else {
                    errorMessage = "Please enter administrator password"
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF00382F), Color(0xFF004D40), Color(0xFF00241E))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // MTU Institutional Logo & Heading
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF00796B))
                    .border(2.5.dp, Color(0xFFFFB300), RoundedCornerShape(18.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "MTU Emblem",
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MIZAN-TEPI UNIVERSITY",
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFD54F),
                letterSpacing = 1.sp
            )
            Text(
                text = "Tepi Campus • Student Dining Access Control",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Department of Information Systems",
                fontSize = 11.sp,
                color = Color(0xFF80CBC4)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Login Card
            ElevatedCard(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SELECT YOUR ACTOR ROLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Actor Switcher Tab Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFECEFF1))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        UserActor.values().forEach { actor ->
                            val isSelected = selectedActor == actor
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF004D40) else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedActor = actor
                                        errorMessage = null
                                        when (actor) {
                                            UserActor.STUDENT -> {
                                                username = "MTU/14022/13"
                                                password = "password123"
                                            }
                                            UserActor.CASHIER -> {
                                                username = "cashier.haile"
                                                password = "password123"
                                            }
                                            UserActor.ADMIN -> {
                                                username = "admin"
                                                password = "admin123"
                                            }
                                        }
                                    }
                                    .padding(vertical = 8.dp)
                                    .testTag("actor_tab_${actor.name.lowercase()}")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = actor.iconEmoji,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = when (actor) {
                                            UserActor.STUDENT -> "Student"
                                            UserActor.CASHIER -> "Cashier"
                                            UserActor.ADMIN -> "Admin"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF37474F)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Role Description Notice
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (selectedActor) {
                            UserActor.STUDENT -> Color(0xFFE0F2F1)
                            UserActor.CASHIER -> Color(0xFFE8F5E9)
                            UserActor.ADMIN -> Color(0xFFFFF3E0)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (selectedActor) {
                                    UserActor.STUDENT -> "🎓 Student View: Only access your personal dynamic QR code, MTU ID, and meal shifts."
                                    UserActor.CASHIER -> "🛡️ Cashier View: Operates turnstile scanner with anti-passback duplicate blocking."
                                    UserActor.ADMIN -> "📊 Admin View: Central campus headcount, student eligibility roster, and daily reports."
                                },
                                fontSize = 11.sp,
                                color = Color(0xFF263238),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = null
                        },
                        label = {
                            Text(
                                when (selectedActor) {
                                    UserActor.STUDENT -> "Student ID (e.g. MTU/14022/13)"
                                    UserActor.CASHIER -> "Cashier Username"
                                    UserActor.ADMIN -> "Admin Username"
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF00796B))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_username_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00796B))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            performLogin(selectedActor, username, password)
                        }),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    // Error Notice
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = errorMessage!!,
                                color = Color(0xFFC62828),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login Action Button
                    Button(
                        onClick = { performLogin(selectedActor, username, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button")
                    ) {
                        Text(
                            text = "Sign In as ${selectedActor.title.split(" ").first()}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(14.dp))

                    // 1-Tap Quick Demo Logins
                    Text(
                        text = "QUICK 1-TAP DEMO SIGN-IN:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78909C),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                performLogin(UserActor.STUDENT, "MTU/14022/13", "password123")
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_login_student")
                        ) {
                            Text("🎓 Sign In as Student (Dejene Gashaw)", fontSize = 12.sp, color = Color(0xFF004D40), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                performLogin(UserActor.CASHIER, "cashier.haile", "password123")
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_login_cashier")
                        ) {
                            Text("🛡️ Sign In as Cashier (Turnstile Scanner)", fontSize = 12.sp, color = Color(0xFF00796B), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                performLogin(UserActor.ADMIN, "admin", "admin123")
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_login_admin")
                        ) {
                            Text("📊 Sign In as Administrator (Cafeteria Mgr)", fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
