package com.example.aurora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.aurora.ui.theme.DeepNightBlue
import com.example.aurora.ui.theme.DarkLavender
import com.example.aurora.ui.theme.VioletGlow

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            DeepNightBlue,
            DarkLavender.copy(alpha = 0.6f),
            VioletGlow.copy(alpha = 0.3f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        content = content
    )
}
