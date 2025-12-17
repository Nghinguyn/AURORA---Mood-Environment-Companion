package com.example.aurora.screens

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.aurora.R
import com.example.aurora.data.AppLanguage
import com.example.aurora.data.SettingsRepository
import com.example.aurora.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var apiKey by remember { mutableStateOf("") }
    var showApiKey by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    
    var selectedLanguage by remember { mutableStateOf(AppLanguage.SYSTEM_DEFAULT) }
    var languageExpanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        apiKey = settingsRepository.getGoogleAiApiKey() ?: ""
        selectedLanguage = settingsRepository.getAppLanguage()
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
                    contentDescription = stringResource(R.string.back),
                    tint = SoftWhite
                )
            }
            Text(
                text = stringResource(R.string.settings),
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
            // Language Section
            Text(
                text = stringResource(R.string.language),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = it }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftWhite.copy(alpha = 0.1f))
                        .clickable { languageExpanded = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getLanguageDisplayName(selectedLanguage),
                            color = SoftWhite,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = SoftWhite.copy(alpha = 0.6f)
                        )
                    }
                }

                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false },
                    modifier = Modifier.background(DeepNightBlue)
                ) {
                    AppLanguage.entries.forEach { language ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = getLanguageDisplayName(language),
                                    color = SoftWhite
                                )
                            },
                            onClick = {
                                selectedLanguage = language
                                languageExpanded = false
                                coroutineScope.launch {
                                    settingsRepository.setAppLanguage(language)
                                    applyLanguage(context, language)
                                }
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = SoftWhite
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // AI Integration Section
            Text(
                text = stringResource(R.string.ai_integration),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(R.string.google_ai_api_key),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SoftWhite.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.api_key_help),
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
                                    text = stringResource(R.string.api_key_hint),
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
                        contentDescription = if (showApiKey) stringResource(R.string.hide) else stringResource(R.string.show),
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
                        saveMessage = context.getString(R.string.api_key_saved)
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
                        text = stringResource(R.string.save),
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
                text = stringResource(R.string.about),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = stringResource(R.string.app_description),
                fontSize = 14.sp,
                color = SoftWhite.copy(alpha = 0.7f)
            )

            Text(
                text = stringResource(R.string.version, "1.0"),
                fontSize = 12.sp,
                color = SoftWhite.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun getLanguageDisplayName(language: AppLanguage): String {
    return when (language) {
        AppLanguage.SYSTEM_DEFAULT -> stringResource(R.string.language_system_default)
        AppLanguage.ENGLISH -> stringResource(R.string.language_english)
        AppLanguage.VIETNAMESE -> stringResource(R.string.language_vietnamese)
    }
}

private fun applyLanguage(context: Context, language: AppLanguage) {
    val localeList = if (language == AppLanguage.SYSTEM_DEFAULT) {
        LocaleListCompat.getEmptyLocaleList()
    } else {
        LocaleListCompat.forLanguageTags(language.code)
    }
    
    AppCompatDelegate.setApplicationLocales(localeList)
}
