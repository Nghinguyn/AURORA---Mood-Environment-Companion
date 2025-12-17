package com.example.aurora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.data.JournalManager
import com.example.aurora.data.JournalResult
import com.example.aurora.data.db.LocationDao
import com.example.aurora.data.db.LocationEntry
import com.example.aurora.data.model.Mood
import com.example.aurora.ui.theme.DeepNightBlue
import com.example.aurora.ui.theme.SoftWhite
import com.example.aurora.ui.theme.AuroraPurple
import com.example.aurora.ui.theme.AuroraGreen
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun JournalEntryScreen(
    journalManager: JournalManager,
    locationDao: LocationDao,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit = onBackClick
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedMoods by remember { mutableStateOf(setOf<Mood>()) }
    var selectedLocation by remember { mutableStateOf<LocationEntry?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val todayLocations by locationDao.observeRecentLocations(20).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNightBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = SoftWhite
                    )
                }
                Text(
                    text = "New Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SoftWhite
                )
            }

            IconButton(
                onClick = {
                    if (!isSaving) {
                        isSaving = true
                        errorMessage = null
                        coroutineScope.launch {
                            val result = journalManager.createEntry(
                                title = title,
                                content = content,
                                moods = selectedMoods.toList(),
                                locationId = selectedLocation?.id
                            )
                            isSaving = false
                            when (result) {
                                is JournalResult.Success -> onSaveSuccess()
                                is JournalResult.Error -> errorMessage = result.message
                            }
                        }
                    }
                },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = SoftWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save",
                        tint = SoftWhite
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Title",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SoftWhite.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BasicTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftWhite.copy(alpha = 0.1f))
                    .padding(16.dp),
                textStyle = TextStyle(
                    color = SoftWhite,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(SoftWhite),
                decorationBox = { innerTextField ->
                    Box {
                        if (title.isEmpty()) {
                            Text(
                                text = "Give your entry a title...",
                                color = SoftWhite.copy(alpha = 0.4f),
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (todayLocations.isNotEmpty()) {
                LocationSelector(
                    locations = todayLocations,
                    selectedLocation = selectedLocation,
                    onLocationSelect = { location ->
                        selectedLocation = if (selectedLocation?.id == location.id) null else location
                    },
                    onWriteAbout = { location ->
                        val prompt = location.placeName ?: location.address ?: "this place"
                        if (content.isEmpty()) {
                            content = "I was at $prompt today...\n\n"
                        } else {
                            content += "\n\nI was also at $prompt...\n"
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "How are you feeling?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SoftWhite.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            MoodSelector(
                selectedMoods = selectedMoods,
                onMoodToggle = { mood ->
                    selectedMoods = if (selectedMoods.contains(mood)) {
                        selectedMoods - mood
                    } else {
                        selectedMoods + mood
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Journal Entry",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SoftWhite.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Supports markdown formatting",
                fontSize = 12.sp,
                color = SoftWhite.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BasicTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftWhite.copy(alpha = 0.1f))
                    .padding(16.dp),
                textStyle = TextStyle(
                    color = SoftWhite,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                ),
                cursorBrush = SolidColor(SoftWhite),
                decorationBox = { innerTextField ->
                    Box {
                        if (content.isEmpty()) {
                            Text(
                                text = "Write your thoughts here...\n\nYou can use **bold**, *italic*, and other markdown formatting.",
                                color = SoftWhite.copy(alpha = 0.4f),
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LocationSelector(
    locations: List<LocationEntry>,
    selectedLocation: LocationEntry?,
    onLocationSelect: (LocationEntry) -> Unit,
    onWriteAbout: (LocationEntry) -> Unit
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }

    Column {
        Text(
            text = "Where were you?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = SoftWhite.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Tap to select, long press to add writing prompt",
            fontSize = 12.sp,
            color = SoftWhite.copy(alpha = 0.4f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val uniqueLocations = locations
            .filter { it.placeName != null || it.address != null }
            .distinctBy { it.placeName ?: it.address }
            .take(5)

        uniqueLocations.forEach { location ->
            val isSelected = selectedLocation?.id == location.id
            val time = remember(location.timestamp) {
                Instant.ofEpochMilli(location.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    .format(timeFormatter)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) AuroraGreen.copy(alpha = 0.25f)
                        else AuroraGreen.copy(alpha = 0.1f)
                    )
                    .then(
                        if (isSelected) Modifier.border(
                            width = 1.dp,
                            color = AuroraGreen,
                            shape = RoundedCornerShape(12.dp)
                        ) else Modifier
                    )
                    .clickable { onLocationSelect(location) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = if (isSelected) AuroraGreen else AuroraGreen.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = location.placeName ?: "Unknown place",
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = SoftWhite
                    )
                    location.address?.let { addr ->
                        Text(
                            text = addr,
                            fontSize = 12.sp,
                            color = SoftWhite.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = SoftWhite.copy(alpha = 0.5f)
                    )
                    if (location.durationMinutes > 0) {
                        Text(
                            text = "${location.durationMinutes} min",
                            fontSize = 11.sp,
                            color = SoftWhite.copy(alpha = 0.4f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "✍️",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onWriteAbout(location) }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uniqueLocations.isEmpty()) {
            Text(
                text = "No locations recorded yet today",
                fontSize = 13.sp,
                color = SoftWhite.copy(alpha = 0.4f),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MoodSelector(
    selectedMoods: Set<Mood>,
    onMoodToggle: (Mood) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Mood.entries.forEach { mood ->
            val isSelected = selectedMoods.contains(mood)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) AuroraPurple.copy(alpha = 0.3f)
                        else SoftWhite.copy(alpha = 0.1f)
                    )
                    .then(
                        if (isSelected) Modifier.border(
                            width = 1.dp,
                            color = AuroraPurple,
                            shape = RoundedCornerShape(20.dp)
                        ) else Modifier
                    )
                    .clickable { onMoodToggle(mood) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = mood.emoji, fontSize = 16.sp)
                    Text(
                        text = mood.label,
                        fontSize = 14.sp,
                        color = if (isSelected) SoftWhite else SoftWhite.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
