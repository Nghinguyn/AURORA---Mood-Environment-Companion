package com.example.aurora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Place
import com.example.aurora.R
import com.example.aurora.data.CalendarManager
import com.example.aurora.data.db.JournalEntry
import com.example.aurora.data.db.LocationEntry
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.data.model.DailySummary
import com.example.aurora.data.model.MonthSummary
import com.example.aurora.data.model.Mood
import com.example.aurora.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    calendarManager: CalendarManager,
    onBackClick: () -> Unit,
    onJournalClick: (Long) -> Unit
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val monthSummary by calendarManager.observeMonthSummary(
        currentYearMonth.year,
        currentYearMonth.monthValue
    ).collectAsState(initial = MonthSummary(currentYearMonth.year, currentYearMonth.monthValue, emptyMap()))

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
                    contentDescription = stringResource(R.string.back),
                    tint = SoftWhite
                )
            }
            Text(
                text = stringResource(R.string.your_journey),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite
            )
        }

        MonthHeader(
            yearMonth = currentYearMonth,
            onPreviousMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
            onNextMonth = { currentYearMonth = currentYearMonth.plusMonths(1) }
        )

        CalendarGrid(
            yearMonth = currentYearMonth,
            monthSummary = monthSummary,
            selectedDate = selectedDate,
            onDateSelected = { date -> selectedDate = date }
        )

        selectedDate?.let { date ->
            val dailySummary = monthSummary.dailySummaries[date]
            Spacer(modifier = Modifier.height(16.dp))
            JournalList(
                date = date,
                dailySummary = dailySummary,
                onJournalClick = onJournalClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MonthHeader(
    yearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.previous_month),
                tint = SoftWhite
            )
        }

        Text(
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = SoftWhite
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(R.string.next_month),
                tint = SoftWhite
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    monthSummary: MonthSummary,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf(
        stringResource(R.string.day_sun),
        stringResource(R.string.day_mon),
        stringResource(R.string.day_tue),
        stringResource(R.string.day_wed),
        stringResource(R.string.day_thu),
        stringResource(R.string.day_fri),
        stringResource(R.string.day_sat)
    )
    val firstDayOfMonth = yearMonth.atDay(1)
    val startDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7)
    val daysInMonth = yearMonth.lengthOfMonth()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = SoftWhite.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        var dayCounter = 1
        val totalCells = startDayOfWeek + daysInMonth
        val weeks = (totalCells + 6) / 7

        for (week in 0 until weeks) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    val cellIndex = week * 7 + dayOfWeek
                    if (cellIndex >= startDayOfWeek && dayCounter <= daysInMonth) {
                        val date = yearMonth.atDay(dayCounter)
                        val summary = monthSummary.dailySummaries[date]
                        val isSelected = date == selectedDate

                        DayCell(
                            day = dayCounter,
                            dominantMood = summary?.dominantMood,
                            hasEntries = summary?.hasJournals == true,
                            isSelected = isSelected,
                            isToday = date == LocalDate.now(),
                            onClick = { onDateSelected(date) },
                            modifier = Modifier.weight(1f)
                        )
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    dominantMood: Mood?,
    hasEntries: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moodColor = when (dominantMood) {
        Mood.HAPPY -> AuroraGreen
        Mood.SAD -> SkyBlue
        Mood.ANXIOUS -> LemonYellow
        Mood.CALM -> SoftBlue
        Mood.ANGRY -> VioletGlow
        Mood.EXCITED -> AuroraGreen
        Mood.TIRED -> DarkLavender
        Mood.GRATEFUL -> AuroraPurple
        Mood.CONFUSED -> LemonYellow
        Mood.HOPEFUL -> SoftBlue
        null -> null
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(
                if (isSelected) Modifier.border(2.dp, AuroraPurple, CircleShape)
                else if (isToday) Modifier.border(1.dp, SoftWhite.copy(alpha = 0.5f), CircleShape)
                else Modifier
            )
            .background(
                when {
                    moodColor != null -> moodColor.copy(alpha = 0.3f)
                    else -> SoftWhite.copy(alpha = 0.05f)
                },
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = SoftWhite
            )
            if (dominantMood != null) {
                Text(
                    text = dominantMood.emoji,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun JournalList(
    date: LocalDate,
    dailySummary: DailySummary?,
    onJournalClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val moodsByJournalId = remember(dailySummary) {
        dailySummary?.moods
            ?.filter { it.journalEntryId != null }
            ?.groupBy { it.journalEntryId!! }
            ?: emptyMap()
    }

    val locationsById = remember(dailySummary) {
        dailySummary?.locations?.associateBy { it.id } ?: emptyMap()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = SoftWhite,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (dailySummary?.journalEntries.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_entries_for_day),
                    color = SoftWhite.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dailySummary.journalEntries) { entry ->
                    val entryMoods = moodsByJournalId[entry.id] ?: emptyList()
                    val entryLocation = entry.locationId?.let { locationsById[it] }
                    JournalPreviewCard(
                        entry = entry,
                        moods = entryMoods,
                        location = entryLocation,
                        onClick = { onJournalClick(entry.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalPreviewCard(
    entry: JournalEntry,
    moods: List<MoodEntry>,
    location: LocationEntry?,
    onClick: () -> Unit
) {
    val previewText = entry.content.take(300).let {
        if (entry.content.length > 300) "$it..." else it
    }

    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a") }
    val entryTime = remember(entry.createdAt) {
        Instant.ofEpochMilli(entry.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
            .format(timeFormatter)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SoftWhite.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SoftWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = entryTime,
                    fontSize = 12.sp,
                    color = SoftWhite.copy(alpha = 0.5f)
                )
            }

            if (location != null) {
                val locationName = remember(location) {
                    val place = location.placeName
                    val addr = location.address
                    when {
                        addr != null -> addr.split(",").take(2).joinToString(",")
                        place != null -> place
                        else -> null
                    }
                }
                
                locationName?.let { name ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = AuroraGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            color = AuroraGreen.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (moods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    moods.map { it.mood }.distinct().forEach { mood ->
                        Text(text = mood.emoji, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = previewText,
                fontSize = 14.sp,
                color = SoftWhite.copy(alpha = 0.7f),
                lineHeight = 20.sp,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
