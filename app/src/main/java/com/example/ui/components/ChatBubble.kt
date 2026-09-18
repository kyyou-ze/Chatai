package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ChatMessageEntity
import com.example.ui.theme.ModernAiBubbleBorderDark
import com.example.ui.theme.ModernAiBubbleBorderLight
import com.example.ui.theme.ModernAiBubbleDark
import com.example.ui.theme.ModernAiBubbleLight
import com.example.ui.theme.ModernIndigoDark
import com.example.ui.theme.ModernIndigoPrimary
import com.example.ui.theme.ModernTextPrimaryDark
import com.example.ui.theme.ModernTextPrimaryLight
import com.example.ui.theme.ModernTextSecondaryDark
import com.example.ui.theme.ModernTextSecondaryLight
import com.example.ui.theme.ModernUserBubbleColor
import com.example.ui.theme.ModernUserBubbleText
import com.example.ui.theme.ModernVioletAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatBubbleItem(
    message: ChatMessageEntity,
    characterName: String,
    isSpeakingThis: Boolean,
    onTogglePlayAudio: () -> Unit
) {
    if (message.role == "user") {
        ModernUserChatBubble(message = message)
    } else {
        ModernCharacterChatBubble(
            message = message,
            characterName = characterName,
            isSpeaking = isSpeakingThis,
            onTogglePlayAudio = onTogglePlayAudio
        )
    }
}

/**
 * Modern Outgoing User Message Bubble
 */
@Composable
fun ModernUserChatBubble(message: ChatMessageEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 56.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
            .testTag("user_chat_bubble_${message.id}"),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 4.dp
            ),
            color = ModernUserBubbleColor,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 80.dp, max = 320.dp)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    color = ModernUserBubbleText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(message.timestamp),
                        fontSize = 11.sp,
                        color = ModernUserBubbleText.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Terkirim",
                        tint = ModernUserBubbleText.copy(alpha = 0.85f),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

/**
 * Modern Incoming Character Message Bubble with Voice Note Audio Player
 */
@Composable
fun ModernCharacterChatBubble(
    message: ChatMessageEntity,
    characterName: String,
    isSpeaking: Boolean,
    onTogglePlayAudio: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val bubbleColor = if (isDark) ModernAiBubbleDark else ModernAiBubbleLight
    val borderColor = if (isDark) ModernAiBubbleBorderDark else ModernAiBubbleBorderLight
    val textColor = if (isDark) ModernTextPrimaryDark else ModernTextPrimaryLight
    val subTextColor = if (isDark) ModernTextSecondaryDark else ModernTextSecondaryLight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 56.dp, top = 4.dp, bottom = 4.dp)
            .testTag("character_chat_bubble_${message.id}"),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Mini Avatar Badge
        Image(
            painter = painterResource(id = R.drawable.character_avatar),
            contentDescription = "Avatar $characterName",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .border(1.5.dp, ModernIndigoPrimary.copy(alpha = 0.5f), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = bubbleColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 90.dp, max = 320.dp)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Character Name Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = characterName,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ModernIndigoPrimary
                    )
                }

                // Message Content
                Text(
                    text = message.content,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Modern Voice Note Player Bar
                ModernVoiceNotePlayer(
                    isSpeaking = isSpeaking,
                    timestamp = message.timestamp,
                    onToggle = onTogglePlayAudio,
                    messageId = message.id
                )
            }
        }
    }
}

/**
 * Sleek Modern Voice Note Player Bar inside AI Bubble
 */
@Composable
fun ModernVoiceNotePlayer(
    isSpeaking: Boolean,
    timestamp: Long,
    onToggle: () -> Unit,
    messageId: Long
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val trackBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = trackBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play / Pause Circle Button
            FilledIconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("audio_play_button_$messageId"),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isSpeaking) ModernVioletAccent else ModernIndigoPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isSpeaking) "Jeda Suara" else "Putar Balasan Suara",
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Animated Waveform Bars
            AnimatedWaveformVisualizer(isPlaying = isSpeaking)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = formatTime(timestamp),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Modern Animated Waveform Bars
 */
@Composable
fun AnimatedWaveformVisualizer(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 16f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(350, delayMillis = 50, easing = LinearEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(450, delayMillis = 100, easing = LinearEasing), RepeatMode.Reverse),
        label = "w3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 14f, targetValue = 7f,
        animationSpec = infiniteRepeatable(tween(380, delayMillis = 150, easing = LinearEasing), RepeatMode.Reverse),
        label = "w4"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.height(22.dp)
    ) {
        val barColor = if (isPlaying) ModernIndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        Box(modifier = Modifier.width(3.dp).height(if (isPlaying) wave1.dp else 8.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
        Box(modifier = Modifier.width(3.dp).height(if (isPlaying) wave2.dp else 14.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
        Box(modifier = Modifier.width(3.dp).height(if (isPlaying) wave3.dp else 10.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
        Box(modifier = Modifier.width(3.dp).height(if (isPlaying) wave4.dp else 16.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
        Box(modifier = Modifier.width(3.dp).height(if (isPlaying) wave2.dp else 8.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
    }
}

/**
 * Modern Animated Typing Indicator
 */
@Composable
fun AiTypingIndicator(characterName: String) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val bubbleColor = if (isDark) ModernAiBubbleDark else ModernAiBubbleLight
    val borderColor = if (isDark) ModernAiBubbleBorderDark else ModernAiBubbleBorderLight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 56.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.character_avatar),
            contentDescription = "Avatar $characterName",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .border(1.5.dp, ModernIndigoPrimary.copy(alpha = 0.4f), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = bubbleColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                JumpingDot(delay = 0)
                JumpingDot(delay = 160)
                JumpingDot(delay = 320)

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$characterName sedang berpikir...",
                    fontSize = 12.5.sp,
                    color = ModernIndigoPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun JumpingDot(delay: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot_jump")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, delayMillis = delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(y = offsetY.dp)
            .size(6.5.dp)
            .clip(CircleShape)
            .background(ModernIndigoPrimary)
    )
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
