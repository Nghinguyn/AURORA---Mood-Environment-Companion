package com.example.aurora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun HomeScreen(
    mood: String,
    onMoodChange: (String) -> Unit,
    isDark: Boolean,
    onToggleDark: (Boolean) -> Unit,
    onNextClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome to AURA",
                fontSize = 26.sp,
                color = if (isDark) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = mood,
                onValueChange = onMoodChange,
                label = { Text("How are you feeling?") },
                modifier = Modifier.width(260.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNextClick) {
                Text("Next")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
