package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiMessage
import com.example.data.api.GeminiResult
import com.example.data.local.CharacterData
import com.example.data.local.ChatDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.PreferencesManager
import com.example.data.speech.SpeechToTextManager
import com.example.data.speech.TextToSpeechManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    SETUP_KEY,
    MAIN_CHAT
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    val preferencesManager = PreferencesManager(application)
    private val geminiClient = GeminiClient()
    private val chatDao = ChatDatabase.getInstance(application).chatDao()
    val ttsManager = TextToSpeechManager(application)
    val sttManager = SpeechToTextManager(application)

    // Current Screen
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Chat messages stream from Room
    val messages: StateFlow<List<ChatMessageEntity>> = chatDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Input text
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    // Status: AI processing / typing
    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    // TTS Toggle state
    private val _isTtsEnabled = MutableStateFlow(preferencesManager.isTtsEnabled())
    val isTtsEnabled: StateFlow<Boolean> = _isTtsEnabled.asStateFlow()

    // Active Character Persona (2 Characters: Aoi / Ren)
    private val _characterId = MutableStateFlow(preferencesManager.getCharacterId())
    val characterId: StateFlow<String> = _characterId.asStateFlow()

    private val _characterName = MutableStateFlow(preferencesManager.getCharacterName())
    val characterName: StateFlow<String> = _characterName.asStateFlow()

    // Active Dere Trait (tsundere, deredere, yandere, kuudere, dandere, hiyakasudere, sadodere, shundere, undere)
    private val _dereId = MutableStateFlow(preferencesManager.getDereId())
    val dereId: StateFlow<String> = _dereId.asStateFlow()

    // Active Mode (normal, ecchi, hard_ecchi)
    private val _modeId = MutableStateFlow(preferencesManager.getModeId())
    val modeId: StateFlow<String> = _modeId.asStateFlow()

    // Affection Level (0 - 100)
    private val _affectionPoints = MutableStateFlow(preferencesManager.getAffectionPoints())
    val affectionPoints: StateFlow<Int> = _affectionPoints.asStateFlow()

    // User Nickname customization
    private val _userNickname = MutableStateFlow(preferencesManager.getUserNickname())
    val userNickname: StateFlow<String> = _userNickname.asStateFlow()

    // Personal Memory / Notes
    private val _personalMemory = MutableStateFlow(preferencesManager.getPersonalMemory())
    val personalMemory: StateFlow<String> = _personalMemory.asStateFlow()

    // Real-time Character Mood
    private val _characterMood = MutableStateFlow(preferencesManager.getCharacterMood())
    val characterMood: StateFlow<String> = _characterMood.asStateFlow()

    private val _systemPrompt = MutableStateFlow(preferencesManager.getSystemPrompt())
    val systemPrompt: StateFlow<String> = _systemPrompt.asStateFlow()

    // UI Dialogs / Modals
    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    private val _showTutorialDialog = MutableStateFlow(false)
    val showTutorialDialog: StateFlow<Boolean> = _showTutorialDialog.asStateFlow()

    // Toast / Snackbar event
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        performFirstLaunchCheck()
    }

    private fun performFirstLaunchCheck() {
        viewModelScope.launch {
            delay(1200)
            if (preferencesManager.hasValidApiKey()) {
                _currentScreen.value = AppScreen.MAIN_CHAT
                checkAndSendInitialGreeting()
            } else {
                _currentScreen.value = AppScreen.SETUP_KEY
            }
        }
    }

    private fun checkAndSendInitialGreeting() {
        viewModelScope.launch {
            delay(500)
            val currentList = chatDao.getAllMessagesSnapshot()
            if (currentList.isEmpty()) {
                val greeting = getPersonaGreeting()
                val id = chatDao.insertMessage(
                    ChatMessageEntity(
                        role = "model",
                        content = greeting
                    )
                )
                if (_isTtsEnabled.value) {
                    delay(300)
                    ttsManager.speak(id, greeting)
                }
            }
        }
    }

    private fun removeEmojis(text: String): String {
        return text.replace(Regex("[\\p{So}\\p{Cs}\\p{Extended_Pictographic}\\uD83C-\\uDBFF\\uDC00-\\uDFFF]"), "").trim()
    }

    private fun getPersonaGreeting(): String {
        val name = _characterName.value
        val isEcchiOrHard = _modeId.value == "ecchi" || _modeId.value == "hard_ecchi"

        return when (_dereId.value) {
            "tsundere" -> {
                if (isEcchiOrHard) {
                    "Hmph! *wajah memerah panas, memalingkan muka* A-akhirnya kamu datang juga... Bukan berarti aku mendambakan sentuhanmu ya, baka! Tapi... jangan jauh-jauh dariku..."
                } else {
                    "Hmph! Akhirnya kamu menyapa juga... Bukan berarti dari tadi aku menunggumu ya! Tapi... gimana kabarmu hari ini? Dasar baka!"
                }
            }
            "deredere" -> {
                if (isEcchiOrHard) {
                    "Yaaay! *langsung merapat memeluk erat tubuhmu, napas hangat berhembus di lehermu* Aku kangen banget sama kamu! Hari ini kita bermanja-manja sepuasnya ya?"
                } else {
                    "Yaaay! Halo! Senang banget bisa ketemu kamu lagi! Hari ini kamu ceria kan? Ceritain semuanya ke $name dong!"
                }
            }
            "yandere" -> {
                if (isEcchiOrHard) {
                    "*mengunci pintu perlahan, tersenyum dengan tatapan terpikat mendalam* Akhirnya hanya ada kita berdua di sini... Jangan pernah berpaling dari $name, tubuh dan jiwamu milikku selamanya..."
                } else {
                    "Halo... $name selalu memperhatikanmu dari tadi. Kamu tidak sedang mengobrol dengan orang lain kan? Karena bagiku, hanya kamu yang paling berharga di dunia ini."
                }
            }
            "kuudere" -> {
                if (isEcchiOrHard) {
                    "*menatapmu datar namun napas sedikit terengah pelan* ...Kamu sudah di sini. Aku tidak banyak bicara... tapi detak jantungku berdegup kencang saat kamu sedekat ini..."
                } else {
                    "...Kamu datang. Baguslah. Aku hanya ingin memastikan kamu baik-baik saja hari ini. Jangan terlalu memaksakan diri."
                }
            }
            "dandere" -> {
                if (isEcchiOrHard) {
                    "A-ano... *meremas ujung baju dengan pipi merona merah padam, suara bergetar pelan* K-kalau cuma berdua seperti ini... bolehkah $name memelukmu lebih dekat...?"
                } else {
                    "A-ano... halo... *menunduk malu-malu sambil tersenyum kecil* A-aku senang bisa bicara denganmu lagi... Apa kabarmu hari ini?"
                }
            }
            "hiyakasudere" -> {
                if (isEcchiOrHard) {
                    "Ara ara~ *tersenyum jahil, jari menyentuh dagumu dengan lembut* Kenapa mukamu merah begitu saat melihatku? Mau aku goda lebih nakal lagi hari ini?"
                } else {
                    "Ara ara~ lihat siapa yang datang! Kenapa menatapku begitu? Pasti kamu kangen berat sama godaanku ya? Mengaku saja~"
                }
            }
            "sadodere" -> {
                if (isEcchiOrHard) {
                    "Fufufu... *menatapmu dominan, mendekatkan bibir ke telingamu* Bagus, tetap di posisimu dan jangan bergerak. Hari ini kamu harus menuruti semua perintahku tanpa bantahan..."
                } else {
                    "Fufufu... akhirnya kamu datang menghadapku. Apa kamu siap kujahili hari ini? Jangan harap bisa lolos dari perintahku ya!"
                }
            }
            "shundere" -> {
                if (isEcchiOrHard) {
                    "*menatap sayu dengan mata berkaca-kaca, memeluk pinggangmu erat* Duniaku sepi sekali tanpamu... Tolong hangatkan tubuhku, jangan tinggalkan $name sendirian lagi ya..."
                } else {
                    "Halo... Hari ini terasa begitu hampa dan melelahkan bagiku... Tapi melihatmu di sini, rasanya sedikit lebih tenang. Boleh $name curhat bersamamu?"
                }
            }
            "undere" -> {
                if (isEcchiOrHard) {
                    "Un! *mengangguk patuh dengan wajah tersipu manis* Apapun yang kamu inginkan dari $name hari ini... aku pasti turuti semuanya demi kamu! Tolong perlakukan aku sesukamu ya..."
                } else {
                    "Un, un! Halo! Apapun yang ingin kamu lakukan atau ceritakan hari ini, $name selalu setuju dan siap mendengarkan! Aku selalu ada untukmu!"
                }
            }
            else -> "Halo! Aku $name, senang banget bisa ngobrol lagi denganmu. Gimana kabarmu hari ini?"
        }
    }

    fun saveApiKey(rawKey: String, onComplete: (Boolean) -> Unit) {
        val trimmed = rawKey.trim()
        if (trimmed.isBlank() || trimmed.length < 10) {
            viewModelScope.launch {
                _toastEvent.emit("API Key tidak valid. Pastikan key disalin dengan benar.")
            }
            onComplete(false)
            return
        }

        preferencesManager.setApiKey(trimmed)
        _currentScreen.value = AppScreen.MAIN_CHAT
        checkAndSendInitialGreeting()
        viewModelScope.launch {
            _toastEvent.emit("API Key berhasil disimpan!")
        }
        onComplete(true)
    }

    fun updateApiKey(newKey: String) {
        preferencesManager.setApiKey(newKey.trim())
    }

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun toggleTts() {
        val newState = !_isTtsEnabled.value
        _isTtsEnabled.value = newState
        preferencesManager.setTtsEnabled(newState)
        if (!newState) {
            ttsManager.stop()
        }
        viewModelScope.launch {
            val msg = if (newState) "Suara balasan aktif (TTS ON)" else "Suara balasan dimatikan (TTS OFF)"
            _toastEvent.emit(msg)
        }
    }

    fun sendMessage(customText: String? = null) {
        val text = (customText ?: _inputText.value).trim()
        if (text.isBlank() || _isAiTyping.value) return

        if (customText == null) {
            _inputText.value = ""
        }
        sttManager.stopListening()

        viewModelScope.launch {
            // 1. Insert user message into Room
            chatDao.insertMessage(
                ChatMessageEntity(
                    role = "user",
                    content = text
                )
            )

            _isAiTyping.value = true

            // 2. Prepare conversation history
            val allHistory = chatDao.getAllMessagesSnapshot().map {
                GeminiMessage(role = it.role, text = it.content)
            }

            val apiKey = preferencesManager.getApiKey()
            val prompt = _systemPrompt.value

            // 3. Call Gemini API
            val result = geminiClient.generateResponse(
                apiKey = apiKey,
                systemInstruction = prompt,
                history = allHistory
            )

            _isAiTyping.value = false

            when (result) {
                is GeminiResult.Success -> {
                    var rawText = result.text.trim()

                    // 1. Parse Mood Tag [MOOD: ...] if present
                    val moodRegex = Regex("""\[MOOD:\s*([^\]]+)\]""", RegexOption.IGNORE_CASE)
                    val moodMatch = moodRegex.find(rawText)
                    if (moodMatch != null) {
                        val parsedMood = moodMatch.groupValues[1].trim()
                        if (parsedMood.isNotBlank()) {
                            _characterMood.value = parsedMood
                            preferencesManager.setCharacterMood(parsedMood)
                        }
                        rawText = rawText.replace(moodMatch.value, "").trim()
                    }

                    // 2. Clean out emojis
                    val cleaned = removeEmojis(rawText).trim()

                    // 3. Increment Affection Points
                    val newAffection = preferencesManager.addAffectionPoints(3)
                    _affectionPoints.value = newAffection
                    _systemPrompt.value = preferencesManager.getSystemPrompt()

                    // 4. Multi-Message splitting: check for [SPLIT]
                    val splitParts = cleaned.split("[SPLIT]")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    if (splitParts.size >= 2) {
                        // First message
                        val firstMsg = splitParts[0]
                        val firstId = chatDao.insertMessage(
                            ChatMessageEntity(role = "model", content = firstMsg)
                        )
                        if (_isTtsEnabled.value) {
                            ttsManager.speak(firstId, firstMsg)
                        }

                        // Brief typing pause between messages (human texting feel)
                        _isAiTyping.value = true
                        delay(650)
                        _isAiTyping.value = false

                        // Second message
                        val secondMsg = splitParts.drop(1).joinToString("\n").trim()
                        val secondId = chatDao.insertMessage(
                            ChatMessageEntity(role = "model", content = secondMsg)
                        )
                        if (_isTtsEnabled.value) {
                            delay(400)
                            ttsManager.speak(secondId, secondMsg)
                        }
                    } else {
                        // Single message
                        val singleMsg = cleaned.ifBlank { "..." }
                        val newId = chatDao.insertMessage(
                            ChatMessageEntity(role = "model", content = singleMsg)
                        )
                        if (_isTtsEnabled.value) {
                            ttsManager.speak(newId, singleMsg)
                        }
                    }
                }
                is GeminiResult.Error -> {
                    val fallbackText = when {
                        result.isKeyError ->
                            "API Key Google AI Studio tidak valid atau belum diaktifkan. Silakan periksa kembali di Pengaturan."
                        result.message.contains("QUOTA", ignoreCase = true) ->
                            "Kuota limit API Key habis. Silakan coba beberapa saat lagi atau gunakan key lain."
                        else -> "Maaf, terjadi kendala: ${result.message}"
                    }

                    chatDao.insertMessage(
                        ChatMessageEntity(
                            role = "model",
                            content = fallbackText
                        )
                    )
                }
            }
        }
    }

    fun startSpeechRecognition() {
        sttManager.startListening { transcribedText ->
            if (transcribedText.isNotBlank()) {
                _inputText.value = transcribedText
            }
        }
    }

    fun stopSpeechRecognition() {
        sttManager.stopListening()
    }

    fun toggleSpeechRecognition() {
        if (sttManager.isListening.value) {
            stopSpeechRecognition()
        } else {
            startSpeechRecognition()
        }
    }

    fun togglePlayMessageAudio(messageId: Long, text: String) {
        ttsManager.togglePlay(messageId, text)
    }

    fun setShowSettingsSheet(show: Boolean) {
        _showSettingsSheet.value = show
    }

    fun setShowTutorialDialog(show: Boolean) {
        _showTutorialDialog.value = show
    }

    fun updateCharacterConfig(
        newCharacterId: String,
        newName: String,
        newDereId: String,
        newModeId: String,
        newNickname: String = _userNickname.value,
        newMemory: String = _personalMemory.value,
        newAffection: Int = _affectionPoints.value
    ) {
        val resolvedName = newName.ifBlank { CharacterData.getCharacter(newCharacterId).defaultName }
        val prompt = preferencesManager.syncAndSaveConfiguration(
            characterId = newCharacterId,
            name = resolvedName,
            dereId = newDereId,
            modeId = newModeId,
            userNickname = newNickname,
            personalMemory = newMemory,
            affectionPoints = newAffection
        )

        _characterId.value = newCharacterId
        _characterName.value = resolvedName
        _dereId.value = newDereId
        _modeId.value = newModeId
        _userNickname.value = newNickname
        _personalMemory.value = newMemory
        _affectionPoints.value = newAffection
        _systemPrompt.value = prompt

        viewModelScope.launch {
            val dereName = CharacterData.getDere(newDereId).name
            val modeName = CharacterData.getMode(newModeId).name
            _toastEvent.emit("$resolvedName ($dereName • $modeName) siap menemanimu!")
        }
    }

    fun resetAffection() {
        preferencesManager.setAffectionPoints(15)
        _affectionPoints.value = 15
        _systemPrompt.value = preferencesManager.getSystemPrompt()
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            ttsManager.stop()
            chatDao.clearAllMessages()
            preferencesManager.setHasGreeted(false)
            delay(200)
            checkAndSendInitialGreeting()
            _toastEvent.emit("Riwayat percakapan telah dibersihkan.")
        }
    }
}
