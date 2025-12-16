package com.example.aurora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.ui.theme.DeepNightBlue
import com.example.aurora.ui.theme.SoftWhite

@Composable
fun CalendarScreen(
    onBackClick: () -> Unit
) {
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
                text = "Your Journey",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Calendar view coming soon.\n\nYour journal entries and moods will appear here.",
                fontSize = 16.sp,
                color = SoftWhite.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
