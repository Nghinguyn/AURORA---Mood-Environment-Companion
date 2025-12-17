package com.example.aurora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.data.SettingsRepository
import com.example.aurora.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onBackClick: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }
    var showApiKey by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        apiKey = settingsRepository.getGoogleAiApiKey() ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNightBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = SoftWhite
                )
            }
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "AI Integration",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Google AI Studio API Key",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SoftWhite.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Get your API key from aistudio.google.com",
                fontSize = 12.sp,
                color = SoftWhite.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftWhite.copy(alpha = 0.1f))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    textStyle = TextStyle(
                        color = SoftWhite,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(SoftWhite),
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (apiKey.isEmpty()) {
                                Text(
                                    text = "Enter your API key...",
                                    color = SoftWhite.copy(alpha = 0.4f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                IconButton(onClick = { showApiKey = !showApiKey }) {
                    Icon(
                        imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showApiKey) "Hide" else "Show",
                        tint = SoftWhite.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isSaving = true
                    saveMessage = null
                    coroutineScope.launch {
                        settingsRepository.setGoogleAiApiKey(apiKey.ifBlank { null })
                        isSaving = false
                        saveMessage = "API key saved"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuroraPurple
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = SoftWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save",
                        color = SoftWhite,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            saveMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = AuroraGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "About",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Aurora - Mood & Environment Companion",
                fontSize = 14.sp,
                color = SoftWhite.copy(alpha = 0.7f)
            )

            Text(
                text = "Version 1.0",
                fontSize = 12.sp,
                color = SoftWhite.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
