package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.AffectionSystem
import com.example.data.local.CharacterData
import com.example.ui.components.AiTypingIndicator
import com.example.ui.components.ApiKeyTutorialDialog
import com.example.ui.components.ChatBubbleItem
import com.example.ui.components.SettingsBottomSheet
import com.example.ui.theme.CleanModeEcchi
import com.example.ui.theme.CleanModeHardEcchi
import com.example.ui.theme.CleanOnlineGreen
import com.example.ui.theme.CleanPrimary
import com.example.ui.theme.CleanRecordingRed
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainChatScreen(
    viewModel: ChatViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State collections
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isAiTyping by viewModel.isAiTyping.collectAsState()
    val isTtsEnabled by viewModel.isTtsEnabled.collectAsState()

    // 2 Characters, 9 Dere traits, 3 Modes
    val characterId by viewModel.characterId.collectAsState()
    val characterName by viewModel.characterName.collectAsState()
    val dereId by viewModel.dereId.collectAsState()
    val modeId by viewModel.modeId.collectAsState()

    // Affection, Mood, Nickname, Personal Memory
    val affectionPoints by viewModel.affectionPoints.collectAsState()
    val characterMood by viewModel.characterMood.collectAsState()
    val userNickname by viewModel.userNickname.collectAsState()
    val personalMemory by viewModel.personalMemory.collectAsState()

    // Audio & STT states
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val currentSpeakingId by viewModel.ttsManager.currentSpeakingMessageId.collectAsState()
    val isListening by viewModel.sttManager.isListening.collectAsState()
    val liveSpokenText by viewModel.sttManager.liveSpokenText.collectAsState()
    val sttError by viewModel.sttManager.errorMessage.collectAsState()

    // Sheet & Dialog States
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showTutorialDialog by remember { mutableStateOf(false) }

    // Recording seconds timer
    var recordingSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(isListening) {
        if (isListening) {
            recordingSeconds = 0
            while (true) {
                delay(1000)
                recordingSeconds++
            }
        } else {
            recordingSeconds = 0
        }
    }

    // STT Error Toast
    LaunchedEffect(sttError) {
        sttError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.sttManager.clearError()
        }
    }

    // Permission launcher for Speech Recognition
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startSpeechRecognition()
        } else {
            Toast.makeText(context, "Izin mikrofon dibutuhkan untuk merekam suara.", Toast.LENGTH_SHORT).show()
        }
    }

    val listState = rememberLazyListState()

    // Auto-scroll on new message
    LaunchedEffect(messages.size, isAiTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Main Layout - Rock-Solid Keyboard Handling
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("main_chat_screen")
    ) {
        // CLEAN TOP BAR WITH PROPERLY ANCHORED DROPDOWN (NO BUGGY POSITIONING)
        CleanTopBar(
            characterName = characterName,
            characterId = characterId,
            dereId = dereId,
            modeId = modeId,
            affectionPoints = affectionPoints,
            characterMood = characterMood,
            isAiTyping = isAiTyping,
            isTtsEnabled = isTtsEnabled,
            onToggleTts = {
                viewModel.toggleTts()
                val status = if (!isTtsEnabled) "diaktifkan" else "dinonaktifkan"
                Toast.makeText(context, "Balasan suara $status", Toast.LENGTH_SHORT).show()
            },
            onOpenSettings = { showSettingsSheet = true },
            onOpenTutorial = { showTutorialDialog = true },
            onClearChat = {
                viewModel.clearChatHistory()
                Toast.makeText(context, "Riwayat obrolan dibersihkan", Toast.LENGTH_SHORT).show()
            }
        )

        // CHAT CONTENT AREA
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
            ) {
                // Quick Starter Prompts based on selected dere
                if (messages.size <= 2) {
                    item(key = "starter_chips") {
                        CleanQuickPromptsCarousel(
                            dereId = dereId,
                            modeId = modeId,
                            onSelectPrompt = { promptText ->
                                viewModel.sendMessage(promptText)
                            }
                        )
                    }
                }

                // Chat Messages List
                items(
                    items = messages,
                    key = { it.id }
                ) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        characterName = characterName,
                        isSpeakingThis = isSpeaking && currentSpeakingId == msg.id,
                        onTogglePlayAudio = {
                            viewModel.ttsManager.togglePlay(msg.id, msg.content)
                        }
                    )
                }

                // Animated AI Typing Indicator
                if (isAiTyping) {
                    item(key = "typing_indicator") {
                        AiTypingIndicator(characterName = characterName)
                    }
                }
            }
        }

        // BOTTOM BAR (CLEAN MATTE INPUT BOX & ANIMATED VOICE NOTE RECORDING)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Animated Recording Bar when mic is active
                androidx.compose.animation.AnimatedVisibility(
                    visible = isListening,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    CleanRecordingBar(
                        seconds = recordingSeconds,
                        recognizedLiveText = liveSpokenText,
                        onCancel = { viewModel.stopSpeechRecognition() },
                        onSend = {
                            val recognized = liveSpokenText.ifBlank { inputText }
                            viewModel.stopSpeechRecognition()
                            if (recognized.isNotBlank()) {
                                viewModel.sendMessage(recognized)
                            }
                        }
                    )
                }

                // Normal Typing Bar when not listening
                androidx.compose.animation.AnimatedVisibility(
                    visible = !isListening,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CleanInputBar(
                        text = inputText,
                        characterName = characterName,
                        onTextChange = { viewModel.onInputTextChanged(it) },
                        onSendMessage = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                            }
                        },
                        onStartRecord = {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                viewModel.startSpeechRecognition()
                            } else {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    )
                }
            }
        }
    }

    // MODAL BOTTOM SHEET: 2 KARAKTER, 9 SIFAT DERE, 3 MODE
    if (showSettingsSheet) {
        SettingsBottomSheet(
            sheetState = sheetState,
            currentCharacterId = characterId,
            currentCharacterName = characterName,
            currentDereId = dereId,
            currentModeId = modeId,
            currentApiKey = viewModel.preferencesManager.getApiKey(),
            currentAffectionPoints = affectionPoints,
            currentUserNickname = userNickname,
            currentPersonalMemory = personalMemory,
            onSaveCharacterConfig = { charId, name, newDereId, newModeId, nickname, memory, affection ->
                viewModel.updateCharacterConfig(charId, name, newDereId, newModeId, nickname, memory, affection)
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showSettingsSheet = false
                }
            },
            onResetAffection = {
                viewModel.resetAffection()
            },
            onSaveApiKey = { newKey ->
                viewModel.updateApiKey(newKey)
                Toast.makeText(context, "API Key berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            },
            onClearHistory = {
                viewModel.clearChatHistory()
            },
            onOpenTutorial = {
                showTutorialDialog = true
            },
            onDismiss = {
                showSettingsSheet = false
            }
        )
    }

    // MODAL TUTORIAL API KEY
    if (showTutorialDialog) {
        ApiKeyTutorialDialog(onDismissRequest = { showTutorialDialog = false })
    }
}

/**
 * Clean Top Bar with Correctly Anchored Dropdown Menu (No Misalignment Bug)
 */
@Composable
fun CleanTopBar(
    characterName: String,
    characterId: String,
    dereId: String,
    modeId: String,
    affectionPoints: Int,
    characterMood: String,
    isAiTyping: Boolean,
    isTtsEnabled: Boolean,
    onToggleTts: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTutorial: () -> Unit,
    onClearChat: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val dere = CharacterData.getDere(dereId)
    val mode = CharacterData.getMode(modeId)
    val tier = AffectionSystem.getTier(affectionPoints)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile Badge & Status (Clickable to open character selection)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onOpenSettings() }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = painterResource(id = R.drawable.character_avatar),
                        contentDescription = "Avatar $characterName",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, CleanPrimary, CircleShape)
                    )
                    // Online Beacon
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(CleanOnlineGreen)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = characterName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Mode Badge (High Contrast, Clean)
                        val modeColor = when (modeId) {
                            "ecchi" -> CleanModeEcchi
                            "hard_ecchi" -> CleanModeHardEcchi
                            else -> CleanPrimary
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = modeColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, modeColor)
                        ) {
                            Text(
                                text = mode.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = modeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Affection Level Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CleanPrimary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CleanPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "Lv.${tier.level} ${tier.title}",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CleanPrimary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isAiTyping) "sedang mengetik..." else "Mood: $characterMood • ${dere.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isAiTyping) CleanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp,
                        fontWeight = if (isAiTyping) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Voice Audio (TTS) Toggle
                IconButton(
                    onClick = onToggleTts,
                    modifier = Modifier.testTag("audio_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isTtsEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = if (isTtsEnabled) "Matikan Balasan Suara" else "Nyalakan Balasan Suara",
                        tint = if (isTtsEnabled) CleanPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Settings Character Button
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("settings_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Pengaturan Karakter",
                        tint = CleanPrimary
                    )
                }

                // ANCHORED DROPDOWN MENU (FIX FOR DROPBAR BUG)
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("more_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu Lainnya",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Directly anchored inside this Box!
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pilih Karakter & Dere") },
                            leadingIcon = {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = CleanPrimary)
                            },
                            onClick = {
                                menuExpanded = false
                                onOpenSettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ganti Gemini API Key") },
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = null, tint = CleanPrimary)
                            },
                            onClick = {
                                menuExpanded = false
                                onOpenSettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Panduan API Key") },
                            leadingIcon = {
                                Icon(Icons.Default.QuestionMark, contentDescription = null, tint = CleanPrimary)
                            },
                            onClick = {
                                menuExpanded = false
                                onOpenTutorial()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Bersihkan Chat", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            },
                            onClick = {
                                menuExpanded = false
                                onClearChat()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clean Input Bar with Non-Neon Styling
 */
@Composable
fun CleanInputBar(
    text: String,
    characterName: String,
    onTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onStartRecord: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Text Input Field
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = "Bicara dengan $characterName...",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .weight(1f)
                .testTag("chat_input_field"),
            shape = RoundedCornerShape(20.dp),
            singleLine = false,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = CleanPrimary,
                unfocusedBorderColor = Color(0xFF94A3B8),
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF0F172A)
            ),
            trailingIcon = {
                IconButton(
                    onClick = onStartRecord,
                    modifier = Modifier.testTag("inline_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Rekam Suara",
                        tint = CleanPrimary
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Send Button
        FilledIconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSendMessage()
                } else {
                    onStartRecord()
                }
            },
            modifier = Modifier
                .size(46.dp)
                .testTag("send_button"),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = CleanPrimary,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = if (text.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                contentDescription = if (text.isNotBlank()) "Kirim Pesan" else "Merekam Suara",
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

/**
 * Clean Non-Neon Recording Bar
 */
@Composable
fun CleanRecordingBar(
    seconds: Int,
    recognizedLiveText: String,
    onCancel: () -> Unit,
    onSend: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_mic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = CleanRecordingRed.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CleanRecordingRed.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Cancel Action
            IconButton(
                onClick = onCancel,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Batal Rekam",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Pulsing Recording Dot & Live Soundwave
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(CleanRecordingRed)
                )

                Spacer(modifier = Modifier.width(8.dp))

                val mins = seconds / 60
                val secs = seconds % 60
                Text(
                    text = String.format("%02d:%02d", mins, secs),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = CleanRecordingRed
                )

                Spacer(modifier = Modifier.width(10.dp))

                CleanLiveSoundwaveVisualizer()

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (recognizedLiveText.isNotBlank()) "\"$recognizedLiveText\"" else "Mendengarkan...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Send Audio Button
            FilledIconButton(
                onClick = onSend,
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = CleanPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Kirim Rekaman",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Clean Live Soundwave Visualizer
 */
@Composable
fun CleanLiveSoundwaveVisualizer() {
    val infiniteTransition = rememberInfiniteTransition(label = "recording_bars")
    val h1 by infiniteTransition.animateFloat(initialValue = 5f, targetValue = 18f, animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse), label = "h1")
    val h2 by infiniteTransition.animateFloat(initialValue = 16f, targetValue = 6f, animationSpec = infiniteRepeatable(tween(250, delayMillis = 40, easing = LinearEasing), RepeatMode.Reverse), label = "h2")
    val h3 by infiniteTransition.animateFloat(initialValue = 7f, targetValue = 20f, animationSpec = infiniteRepeatable(tween(350, delayMillis = 80, easing = LinearEasing), RepeatMode.Reverse), label = "h3")
    val h4 by infiniteTransition.animateFloat(initialValue = 17f, targetValue = 8f, animationSpec = infiniteRepeatable(tween(280, delayMillis = 120, easing = LinearEasing), RepeatMode.Reverse), label = "h4")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.height(20.dp)
    ) {
        Box(modifier = Modifier.width(3.dp).height(h1.dp).clip(RoundedCornerShape(2.dp)).background(CleanRecordingRed))
        Box(modifier = Modifier.width(3.dp).height(h2.dp).clip(RoundedCornerShape(2.dp)).background(CleanRecordingRed))
        Box(modifier = Modifier.width(3.dp).height(h3.dp).clip(RoundedCornerShape(2.dp)).background(CleanRecordingRed))
        Box(modifier = Modifier.width(3.dp).height(h4.dp).clip(RoundedCornerShape(2.dp)).background(CleanRecordingRed))
    }
}

/**
 * Quick Starter Prompts based on Dere Trait & Mode
 */
@Composable
fun CleanQuickPromptsCarousel(
    dereId: String,
    modeId: String,
    onSelectPrompt: (String) -> Unit
) {
    val prompts = when (modeId) {
        "ecchi", "hard_ecchi" -> listOf(
            "Boleh aku memelukmu sekarang?",
            "Kenapa menatapku seperti itu?",
            "Bisikkan sesuatu yang manis di telingaku...",
            "Kamu kelihatan sangat menggoda hari ini",
            "Mendekatlah ke sini bersamaku..."
        )
        else -> when (dereId) {
            "tsundere" -> listOf(
                "Kenapa cemberut begitu?",
                "Aku kangen kamu lho hari ini",
                "Jangan pura-pura cuek dong!",
                "Hari ini kamu lagi sibuk apa?"
            )
            "yandere" -> listOf(
                "Aku hanya milikmu kok",
                "Apa yang sedang kamu pikirkan?",
                "Jangan cemburu ya, aku selalu di sini",
                "Ceritakan apa yang kamu suka dariku"
            )
            "dandere" -> listOf(
                "Hai... jangan malu-malu ya",
                "Apa kabar harimu hari ini?",
                "Boleh aku mendengar suaramu?",
                "Cerita apa saja, aku siap dengar"
            )
            else -> listOf(
                "Hai! Kenalkan dirimu dong",
                "Ceritakan harimu hari ini",
                "Lagi kepikiran apa nih?",
                "Beri aku kata-kata penyemangat"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = "Mulai percakapan:",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(prompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.clickable { onSelectPrompt(prompt) }
                ) {
                    Text(
                        text = prompt,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CleanPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}
