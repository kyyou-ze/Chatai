package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AffectionSystem
import com.example.data.local.CharacterData
import com.example.ui.theme.CleanModeEcchi
import com.example.ui.theme.CleanModeHardEcchi
import com.example.ui.theme.CleanPrimary
import com.example.ui.theme.CleanRecordingRed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsBottomSheet(
    sheetState: SheetState,
    currentCharacterId: String,
    currentCharacterName: String,
    currentDereId: String,
    currentModeId: String,
    currentApiKey: String,
    currentAffectionPoints: Int = 15,
    currentUserNickname: String = "",
    currentPersonalMemory: String = "",
    onSaveCharacterConfig: (
        characterId: String,
        name: String,
        dereId: String,
        modeId: String,
        userNickname: String,
        personalMemory: String,
        affectionPoints: Int
    ) -> Unit,
    onResetAffection: () -> Unit = {},
    onSaveApiKey: (newKey: String) -> Unit,
    onClearHistory: () -> Unit,
    onOpenTutorial: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var selectedCharacterId by remember(currentCharacterId) { mutableStateOf(currentCharacterId) }
    var editedName by remember(currentCharacterName) { mutableStateOf(currentCharacterName) }
    var selectedDereId by remember(currentDereId) { mutableStateOf(currentDereId) }
    var selectedModeId by remember(currentModeId) { mutableStateOf(currentModeId) }
    var userNicknameInput by remember(currentUserNickname) { mutableStateOf(currentUserNickname) }
    var personalMemoryInput by remember(currentPersonalMemory) { mutableStateOf(currentPersonalMemory) }
    var affectionPointsVal by remember(currentAffectionPoints) { mutableIntStateOf(currentAffectionPoints) }

    var apiKeyInput by remember { mutableStateOf("") }
    var isApiKeyVisible by remember { mutableStateOf(false) }
    var showApiKeyField by remember { mutableStateOf(false) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Clean and Muted
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CleanPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = CleanPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Pengaturan Karakter",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pilih karakter, sifat dere, dan mode interaksi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 1: PILIHAN KARAKTER (2 SAJA)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Pilih Karakter (2 Pilihan)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pilih pasangan karakter obrolan yang kamu inginkan:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CharacterData.characters.forEach { charProfile ->
                            val isSelected = selectedCharacterId == charProfile.id
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) CleanPrimary else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isSelected) CleanPrimary else Color(0xFF94A3B8)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedCharacterId = charProfile.id
                                        if (editedName == "Aoi" || editedName == "Ren" || editedName == "Yuna" || editedName.isBlank()) {
                                            editedName = charProfile.defaultName
                                        }
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White.copy(alpha = 0.2f) else CleanPrimary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = charProfile.defaultName.take(1),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = if (isSelected) Color.White else CleanPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = charProfile.defaultName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else CleanPrimary
                                    )
                                    Text(
                                        text = charProfile.gender,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nama Panggilan Kustom
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nama Panggilan Karakter") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = CleanPrimary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("character_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CleanPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 2: PEMILIHAN SIFAT KARAKTER (9 DERE ARCHETYPES)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Sifat Karakter (Dere Archetype)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tentukan pembawaan emosi dan kepribadian karakter:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CharacterData.dereTraits.forEach { dere ->
                            val isSelected = selectedDereId == dere.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDereId = dere.id },
                                label = {
                                    Text(dere.name, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CleanPrimary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = CleanPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) CleanPrimary else Color(0xFF94A3B8),
                                    borderWidth = 1.2.dp
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // Deskripsi Sifat yang Dipilih
                    val activeDere = CharacterData.getDere(selectedDereId)
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${activeDere.name}: ${activeDere.description}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CleanPrimary,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 3: MODE KARAKTER (NORMAL, ECCHI, HARD ECCHI)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFCBD5E1)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Mode Obrolan (Tingkat Sensualitas)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Atur intensitas suasana dan gaya bahasa karakter:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CharacterData.modes.forEach { mode ->
                            val isSelected = selectedModeId == mode.id
                            val activeBg = when (mode.id) {
                                "ecchi" -> CleanModeEcchi
                                "hard_ecchi" -> CleanModeHardEcchi
                                else -> CleanPrimary
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) activeBg else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isSelected) activeBg else Color(0xFF94A3B8)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedModeId = mode.id }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = mode.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else activeBg
                                    )
                                }
                            }
                        }
                    }

                    // Deskripsi Mode yang Dipilih
                    val activeMode = CharacterData.getMode(selectedModeId)
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Mode ${activeMode.name}: ${activeMode.description}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CleanPrimary,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 4: SISTEM TINGKAT KEDEKATAN (AFFECTION SYSTEM)
            val currentTier = AffectionSystem.getTier(affectionPointsVal)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFCBD5E1)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "4. Tingkat Kedekatan (Affection)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hubungan berkembang seiring intensitas mengobrol",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CleanPrimary,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = "Level ${currentTier.level}: ${currentTier.title}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Poin Kedekatan",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$affectionPointsVal / 100",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = CleanPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { affectionPointsVal / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CleanPrimary,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentTier.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                affectionPointsVal = 15
                                onResetAffection()
                                Toast.makeText(context, "Tingkat kedekatan di-reset ke Kenalan (15 poin)", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset Kedekatan", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 5: KUSTOMISASI PANGGILAN & MEMORI PERSONAL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFCBD5E1)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5. Panggilan & Memori Personal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sesuaikan panggilan khusus dan hal yang harus selalu diingat karakter:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input Panggilan Khusus
                    OutlinedTextField(
                        value = userNicknameInput,
                        onValueChange = { userNicknameInput = it },
                        label = { Text("Panggilan untukmu oleh Karakter") },
                        placeholder = { Text("Contoh: Senpai, Sayang, Kakak, Tuan, atau namamu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CleanPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input Memori Personal
                    OutlinedTextField(
                        value = personalMemoryInput,
                        onValueChange = { personalMemoryInput = it },
                        label = { Text("Memori Personal / Catatan Rahasia") },
                        placeholder = { Text("Contoh: Aku suka kopi tanpa gula, hobi main game anime, dan gampang tersipu jika dipuji...") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CleanPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TOMBOL TERAPKAN PERUBAHAN KARAKTER
            Button(
                onClick = {
                    onSaveCharacterConfig(
                        selectedCharacterId,
                        editedName,
                        selectedDereId,
                        selectedModeId,
                        userNicknameInput,
                        personalMemoryInput,
                        affectionPointsVal
                    )
                    Toast.makeText(context, "Karakter '$editedName' dan preferensi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_character_config_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CleanPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Terapkan & Simpan Semua Pengaturan", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 4: KELOLA API KEY
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = CleanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google AI Studio API Key",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val maskedKey = if (currentApiKey.length > 8) {
                        currentApiKey.take(4) + "••••••••••••" + currentApiKey.takeLast(4)
                    } else if (currentApiKey.isNotBlank()) {
                        "••••••••••••"
                    } else {
                        "Belum diatur"
                    }

                    Text(
                        text = "Status: $maskedKey",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!showApiKeyField) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showApiKeyField = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Ganti API Key")
                            }
                            OutlinedButton(
                                onClick = onOpenTutorial,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Panduan")
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it.trim() },
                            label = { Text("Masukkan API Key Baru") },
                            placeholder = { Text("AIzaSy...") },
                            visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                                    Icon(
                                        imageVector = if (isApiKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (apiKeyInput.length >= 10) {
                                        onSaveApiKey(apiKeyInput)
                                        showApiKeyField = false
                                        apiKeyInput = ""
                                    } else {
                                        Toast.makeText(context, "API Key terlalu pendek.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = CleanPrimary)
                            ) {
                                Text("Simpan Key")
                            }

                            OutlinedButton(
                                onClick = {
                                    showApiKeyField = false
                                    apiKeyInput = ""
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Batal")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 5: BERSIHKAN RIWAYAT
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Riwayat Obrolan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bersihkan riwayat percakapan untuk memulai interaksi baru dengan karakter pilihanmu.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showDeleteConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CleanRecordingRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bersihkan Semua Riwayat Chat")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hapus Riwayat Chat?") },
            text = { Text("Semua pesan yang tersimpan di perangkat ini akan dihapus secara permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "Riwayat percakapan telah dibersihkan.", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Ya, Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
