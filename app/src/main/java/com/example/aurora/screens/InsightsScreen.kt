package com.example.aurora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.R
import com.example.aurora.data.InsightResult
import com.example.aurora.data.InsightsRepository
import com.example.aurora.ui.theme.AuroraGreen
import com.example.aurora.ui.theme.DeepNightBlue
import com.example.aurora.ui.theme.LemonYellow
import com.example.aurora.ui.theme.SoftWhite
import com.example.aurora.ui.theme.VioletGlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    insightsRepository: InsightsRepository,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var insightResult by remember { mutableStateOf<InsightResult?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        insightResult = insightsRepository.generateInsights(forceRefresh = false)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNightBlue)
    ) {
        TopAppBar(
            title = {
                Text(
                    stringResource(R.string.insights_report_title),
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = SoftWhite
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            insightResult = insightsRepository.generateInsights(forceRefresh = true)
                            isLoading = false
                        }
                    },
                    enabled = !isLoading
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh),
                        tint = if (isLoading) SoftWhite.copy(alpha = 0.5f) else SoftWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DeepNightBlue
            )
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = VioletGlow)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.analyzing_patterns),
                        color = SoftWhite.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                insightResult?.error?.let { error ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LemonYellow.copy(alpha = 0.2f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = error,
                            color = LemonYellow,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                insightResult?.let { result ->
                    if (result.summary.isNotBlank()) {
                        SummaryCard(result.summary)
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    if (result.fullReport.isNotBlank()) {
                        MarkdownReport(result.fullReport)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VioletGlow.copy(alpha = 0.2f))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.key_insight),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AuroraGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = summary,
                fontSize = 16.sp,
                color = SoftWhite,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun MarkdownReport(report: String) {
    val sections = parseMarkdownSections(report)

    sections.forEach { section ->
        when {
            section.startsWith("## ") -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = section.removePrefix("## ").trim(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VioletGlow
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            section.startsWith("### ") -> {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = section.removePrefix("### ").trim(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AuroraGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            section.startsWith("- ") || section.startsWith("* ") -> {
                Row(
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Text("•", color = VioletGlow, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = section.removePrefix("- ").removePrefix("* ").trim(),
                        fontSize = 14.sp,
                        color = SoftWhite.copy(alpha = 0.9f),
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            section.startsWith(">") -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(SoftWhite.copy(alpha = 0.05f))
                        .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = section.removePrefix(">").trim(),
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = SoftWhite.copy(alpha = 0.7f)
                    )
                }
            }
            section.isNotBlank() -> {
                Text(
                    text = section.trim(),
                    fontSize = 14.sp,
                    color = SoftWhite.copy(alpha = 0.85f),
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

private fun parseMarkdownSections(markdown: String): List<String> {
    val lines = markdown.split("\n")
    val sections = mutableListOf<String>()
    val currentParagraph = StringBuilder()

    for (line in lines) {
        when {
            line.startsWith("## ") || line.startsWith("### ") -> {
                if (currentParagraph.isNotBlank()) {
                    sections.add(currentParagraph.toString().trim())
                    currentParagraph.clear()
                }
                sections.add(line)
            }
            line.startsWith("- ") || line.startsWith("* ") || line.startsWith(">") -> {
                if (currentParagraph.isNotBlank()) {
                    sections.add(currentParagraph.toString().trim())
                    currentParagraph.clear()
                }
                sections.add(line)
            }
            line.isBlank() -> {
                if (currentParagraph.isNotBlank()) {
                    sections.add(currentParagraph.toString().trim())
                    currentParagraph.clear()
                }
            }
            else -> {
                if (currentParagraph.isNotBlank()) {
                    currentParagraph.append(" ")
                }
                currentParagraph.append(line)
            }
        }
    }

    if (currentParagraph.isNotBlank()) {
        sections.add(currentParagraph.toString().trim())
    }

    return sections
}
