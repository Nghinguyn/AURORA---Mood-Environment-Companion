package com.example.aurora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReflectionScreen(
    reflectionMessage: String,
    onViewJourney: () -> Unit
) {
    val auroraGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF2C5364))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(auroraGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = reflectionMessage,
                    modifier = Modifier.padding(24.dp),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onViewJourney,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("View Journey")
            }
        }
    }
}
