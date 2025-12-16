package com.example.aurora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.ui.theme.AuroraGreen
import com.example.aurora.ui.theme.DeepNightBlue
import com.example.aurora.ui.theme.SoftWhite

@Composable
fun JournalEntryCard(
    todayEntryCount: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0d4a4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "New Journal Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Capture your thoughts",
                    fontSize = 14.sp,
                    color = SoftWhite.copy(alpha = 0.6f)
                )
                if (todayEntryCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$todayEntryCount entries today",
                        fontSize = 12.sp,
                        color = AuroraGreen
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AuroraGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = DeepNightBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
