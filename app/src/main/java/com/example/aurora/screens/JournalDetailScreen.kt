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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.R
import com.example.aurora.data.JournalManager
import com.example.aurora.data.JournalResult
import com.example.aurora.data.db.JournalEntry
import com.example.aurora.data.db.LocationDao
import com.example.aurora.data.db.LocationEntry
import com.example.aurora.data.db.MoodDao
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.data.model.Mood
import com.example.aurora.ui.theme.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

@Composable
fun JournalDetailScreen(
    journalId: Long,
    journalManager: JournalManager,
    moodDao: MoodDao,
    locationDao: LocationDao,
    onBackClick: () -> Unit,
    onDeleted: () -> Unit
) {
    var entry by remember { mutableStateOf<JournalEntry?>(null) }
    var moods by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var location by remember { mutableStateOf<LocationEntry?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var editTitle by remember { mutableStateOf("") }
    var editContent by remember { mutableStateOf("") }
    var editMoods by remember { mutableStateOf(setOf<Mood>()) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(journalId) {
        when (val result = journalManager.getEntryWithMoods(journalId)) {
            is JournalResult.Success -> {
                entry = result.data.entry
                moods = result.data.moods
                editTitle = result.data.entry.title
                editContent = result.data.entry.content
                editMoods = result.data.moods.map { it.mood }.toSet()
                result.data.entry.locationId?.let { locId ->
                    location = locationDao.getLocationsForDate(result.data.entry.date)
                        .find { it.id == locId }
                }
            }
            is JournalResult.Error -> errorMessage = result.message
        }
        isLoading = false
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_entry), color = SoftWhite) },
            text = { Text(stringResource(R.string.delete_confirm), color = SoftWhite.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            journalManager.deleteEntry(journalId)
                            showDeleteDialog = false
                            onDeleted()
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel), color = SoftWhite)
                }
            },
            containerColor = DeepNightBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNightBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = (if (isEditing) {{
                    isEditing = false
                    entry?.let {
                        editTitle = it.title
                        editContent = it.content
                        editMoods = moods.map { m -> m.mood }.toSet()
                    }
                }} else onBackClick) as () -> Unit) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Close else Icons.Default.ArrowBack,
                        contentDescription = if (isEditing) stringResource(R.string.cancel) else stringResource(R.string.back),
                        tint = SoftWhite
                    )
                }
            }

            Row {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                
                if (isEditing) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val result = journalManager.updateEntry(
                                    id = journalId,
                                    title = editTitle,
                                    content = editContent,
                                    moods = editMoods.toList(),
                                    locationId = entry?.locationId
                                )
                                when (result) {
                                    is JournalResult.Success -> {
                                        isEditing = false
                                        when (val refreshResult = journalManager.getEntryWithMoods(journalId)) {
                                            is JournalResult.Success -> {
                                                entry = refreshResult.data.entry
                                                moods = refreshResult.data.moods
                                            }
                                            is JournalResult.Error -> {}
                                        }
                                    }
                                    is JournalResult.Error -> errorMessage = result.message
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save),
                            tint = AuroraGreen
                        )
                    }
                } else {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = SoftWhite
                        )
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SoftWhite)
            }
        } else if (entry == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage ?: stringResource(R.string.entry_not_found),
                    color = SoftWhite.copy(alpha = 0.6f)
                )
            }
        } else {
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

                val currentEntry = entry!!
                val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy") }
                val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }

                Text(
                    text = currentEntry.date.format(dateFormatter),
                    fontSize = 12.sp,
                    color = SoftWhite.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = Instant.ofEpochMilli(currentEntry.createdAt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime()
                        .format(timeFormatter),
                    fontSize = 12.sp,
                    color = SoftWhite.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isEditing) {
                    BasicTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftWhite.copy(alpha = 0.1f))
                            .padding(16.dp),
                        textStyle = TextStyle(
                            color = SoftWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        cursorBrush = SolidColor(SoftWhite)
                    )
                } else {
                    Text(
                        text = currentEntry.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftWhite
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                location?.let { loc ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = AuroraGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = loc.address ?: loc.placeName ?: stringResource(R.string.unknown_location),
                            fontSize = 14.sp,
                            color = AuroraGreen.copy(alpha = 0.8f)
                        )
                    }
                }

                if (isEditing) {
                    Text(
                        text = stringResource(R.string.moods),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SoftWhite.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    EditMoodSelector(
                        selectedMoods = editMoods,
                        onMoodToggle = { mood ->
                            editMoods = if (editMoods.contains(mood)) {
                                editMoods - mood
                            } else {
                                editMoods + mood
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    if (moods.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            moods.map { it.mood }.distinct().forEach { mood ->
                                Text(text = "${mood.emoji} ${stringResource(mood.labelResId)}", fontSize = 14.sp, color = SoftWhite.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                if (isEditing) {
                    Text(
                        text = stringResource(R.string.content),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SoftWhite.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    BasicTextField(
                        value = editContent,
                        onValueChange = { editContent = it },
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
                        cursorBrush = SolidColor(SoftWhite)
                    )
                } else {
                    Text(
                        text = currentEntry.content,
                        fontSize = 16.sp,
                        color = SoftWhite.copy(alpha = 0.9f),
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditMoodSelector(
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
                        text = stringResource(mood.labelResId),
                        fontSize = 14.sp,
                        color = if (isSelected) SoftWhite else SoftWhite.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
