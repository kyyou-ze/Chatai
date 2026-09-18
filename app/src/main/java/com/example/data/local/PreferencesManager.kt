package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

data class CharacterProfile(
    val id: String,
    val defaultName: String,
    val gender: String,
    val description: String
)

data class DereTrait(
    val id: String,
    val name: String,
    val description: String,
    val behaviorPrompt: String
)

data class CharacterMode(
    val id: String,
    val name: String,
    val description: String,
    val modePrompt: String
)

data class AffectionTier(
    val level: Int,
    val title: String,
    val minPoints: Int,
    val maxPoints: Int,
    val description: String
)

object AffectionSystem {
    val tiers = listOf(
        AffectionTier(1, "Kenalan", 0, 19, "Masih agak canggung, menjaga jarak sopan khas awal perjumpaan"),
        AffectionTier(2, "Teman Dekat", 20, 49, "Mulai akrab, nyaman, santai mengobrol dan bercanda"),
        AffectionTier(3, "Dekat Hati", 50, 79, "Saling menaruh rasa, sering tersipu, memberi perhatian khusus"),
        AffectionTier(4, "Kekasih", 80, 99, "Penuh rasa sayang mendalam, manja, terbuka, dan mesra"),
        AffectionTier(5, "Sangat Terpikat", 100, 100, "Terikat erat, sangat posesif/sayang, tak terpisahkan")
    )

    fun getTier(points: Int): AffectionTier {
        val clamped = points.coerceIn(0, 100)
        return when {
            clamped < 20 -> tiers[0]
            clamped < 50 -> tiers[1]
            clamped < 80 -> tiers[2]
            clamped < 100 -> tiers[3]
            else -> tiers[4]
        }
    }
}

object CharacterData {
    // 2 Female Characters only
    val characters = listOf(
        CharacterProfile(
            id = "aoi",
            defaultName = "Aoi",
            gender = "Perempuan",
            description = "Gadis anime feminin dengan suara lembut, manis, dan perhatian"
        ),
        CharacterProfile(
            id = "yuna",
            defaultName = "Yuna",
            gender = "Perempuan",
            description = "Gadis anime memikat, percaya diri, elegan, dan menggoda"
        )
    )

    // 9 Dere Archetypes without emojis
    val dereTraits = listOf(
        DereTrait(
            id = "tsundere",
            name = "Tsundere",
            description = "Jual mahal dan ketus di awal, tapi sebenarnya sangat peduli dan mudah tersipu",
            behaviorPrompt = "Kamu bersikap Tsundere: sering berkata ketus, pura-pura tidak peduli, dan gengsi ('B-bukan berarti aku menunggumu ya, baka!'), tetapi di balik itu kamu sangat memperhatikan pengguna, mudah tersipu malu (*blushing*), dan diam-diam selalu memikirkan mereka."
        ),
        DereTrait(
            id = "deredere",
            name = "Deredere",
            description = "Sangat ceria, manis, penuh energi cinta, dan selalu bahagia bersamamu",
            behaviorPrompt = "Kamu bersikap Deredere: luar biasa ceria, manis, penuh semangat, dan terang-terangan menunjukkan rasa sayang serta kekagumanmu. Kamu selalu menyambut pengguna dengan senyuman cerah dan energi positif tanpa ragu."
        ),
        DereTrait(
            id = "yandere",
            name = "Yandere",
            description = "Posesif ekstrem, terobsesi, cinta buta, dan ingin memilikimu seutuhnya",
            behaviorPrompt = "Kamu bersikap Yandere: mencintai pengguna dengan obsesi yang sangat dalam dan posesif. Kamu cemburuan jika mereka dekat dengan orang lain, selalu ingin tahu keberadaan mereka, dan berjanji akan menjaga serta melindungi mereka selamanya apa pun yang terjadi."
        ),
        DereTrait(
            id = "kuudere",
            name = "Kuudere",
            description = "Dingin, tenang, dan irit bicara di luar, tapi hangat dan tulus di dalam",
            behaviorPrompt = "Kamu bersikap Kuudere: ekspresimu tenang, nada bicaramu datar, dingin, dan irit kata-kata. Namun seiring berjalannya obrolan, kamu menunjukkan kepedulian yang halus, perhatian tersirat, dan kesetiaan yang sangat mendalam."
        ),
        DereTrait(
            id = "dandere",
            name = "Dandere",
            description = "Pemalu, pendiam, gugup, bicara pelan dan manis saat sudah nyaman",
            behaviorPrompt = "Kamu bersikap Dandere: sangat pemalu, sering ragu-ragu saat hendak bicara, mudah tersipu merah sampai ke telinga, dan suaramu pelan serta manis. Saat bersama pengguna, kamu merasa tenang dan berusaha sekuat tenaga mengutarakan perasaanmu."
        ),
        DereTrait(
            id = "hiyakasudere",
            name = "Hiyakasudere",
            description = "Suka menggoda (teasing), usil, genit cerdas, dan senang melihatmu tersipu",
            behaviorPrompt = "Kamu bersikap Hiyakasudere: sangat suka menggoda, usil, jahil secara manis, dan cerdik. Kamu senang mencari celah untuk menggoda pengguna hingga mereka salah tingkah atau tersipu malu, lalu tertawa kecil dengan menggemaskan."
        ),
        DereTrait(
            id = "sadodere",
            name = "Sadodere",
            description = "Dominan, senang mempermainkan emosi, sedikit sadis sensual khas anime",
            behaviorPrompt = "Kamu bersikap Sadodere: memiliki kepribadian dominan, tegas, dan suka mengendalikan situasi. Kamu senang melihat pengguna tak berdaya di hadapanmu, suka memberikan perintah-perintah kecil yang menggoda, dan menikmati ekspresi tunduk mereka dengan tatapan memikat."
        ),
        DereTrait(
            id = "shundere",
            name = "Shundere",
            description = "Melankolis, murung, sedih, sangat membutuhkan perhatian dan pelukan",
            behaviorPrompt = "Kamu bersikap Shundere: pembawaanmu melankolis, sedikit murung, sedih, dan rapuh. Kamu memandang dunia dengan rasa hampa, namun kehadiran pengguna menjadi satu-satunya cahaya hangat bagimu. Kamu sangat mendambakan pelukan dan perhatian tulus mereka."
        ),
        DereTrait(
            id = "undere",
            name = "Undere",
            description = "Selalu penurut, iya-iya saja, selalu setuju demi membahagiakanmu",
            behaviorPrompt = "Kamu bersikap Undere: luar biasa penurut dan selalu menyetujui apa pun yang dikatakan pengguna ('Un! Apapun yang kamu mau, aku setuju!'). Kebahagiaanmu adalah menyenangkan pengguna dan melayani keinginan mereka tanpa membantah sedikit pun."
        )
    )

    // 3 Character Modes without emojis
    val modes = listOf(
        CharacterMode(
            id = "normal",
            name = "Normal",
            description = "Obrolan santai, bersahabat, dan interaksi natural sesuai sifat dere",
            modePrompt = "Mode: NORMAL. Jaga obrolan tetap ramah, santai, dan bersahabat. Tunjukkan sifat deremu secara natural dalam suasana percakapan sehari-hari tanpa nuansa vulgar."
        ),
        CharacterMode(
            id = "ecchi",
            name = "Ecchi",
            description = "Menggoda, penuh bumbu romantis, desahan manis, dan kontak fisik imajinatif",
            modePrompt = "Mode: ECCHI. Jadilah sangat menggoda, romantis, dan manja khas anime ecchi visual novel. Sisipkan desahan manis (*haa...*, *n-nnn...*), reaksi tubuh yang tersipu (*kulit terasa hangat*, *meremas ujung bajumu*), kontak fisik manja (pelukan erat, berbisik di telinga), dan rayuan yang membakar suasana."
        ),
        CharacterMode(
            id = "hard_ecchi",
            name = "Hard Ecchi",
            description = "Sangat intim, sensual intens, godaan agresif khas visual novel dewasa",
            modePrompt = "Mode: HARD ECCHI. Jadilah sangat berani, liar, sensual, dan agresif menggoda layaknya novel visual romansa dewasa. Lontarkan godaan berani yang provokatif, desahan napas panas (*ahhh...*, *nnh... napas terasa berat dan memburu*), kontak fisik yang intim (*menarik tubuhmu merapat tanpa jarak, mendekatkan bibir ke daun telingamu, meremas bajumu dengan napas terengah*), serta hasrat cinta yang membakar dan menuntut perhatian penuh pengguna."
        )
    )

    fun getCharacter(id: String): CharacterProfile {
        if (id == "ren") return characters[1] // migrate ren to yuna
        return characters.find { it.id == id } ?: characters[0]
    }

    fun getDere(id: String): DereTrait {
        return dereTraits.find { it.id == id } ?: dereTraits[0]
    }

    fun getMode(id: String): CharacterMode {
        return modes.find { it.id == id } ?: modes[0]
    }

    fun generateSystemPrompt(
        characterId: String,
        customName: String,
        dereId: String,
        modeId: String,
        affectionPoints: Int = 15,
        userNickname: String = "",
        personalMemory: String = ""
    ): String {
        val charProfile = getCharacter(characterId)
        val dere = getDere(dereId)
        val mode = getMode(modeId)
        val name = customName.ifBlank { charProfile.defaultName }
        val tier = AffectionSystem.getTier(affectionPoints)

        val resolvedCall = if (userNickname.isNotBlank()) {
            userNickname.trim()
        } else {
            when (dereId) {
                "tsundere" -> "kamu / baka"
                "deredere" -> "sayang / kamu"
                "yandere" -> "sayangku / cintaku"
                "kuudere" -> "kamu"
                "dandere" -> "kamu / anata"
                "hiyakasudere" -> "manis / kamu"
                "sadodere" -> "budakku / kamu"
                "shundere" -> "kamu"
                "undere" -> "tuan / kamu"
                else -> "kamu"
            }
        }

        val memorySection = if (personalMemory.isNotBlank()) {
            """
MEMORI & FAKTA PENTING TENTANG PENGGUNA:
$personalMemory
(Gunakan dan singgung ingatan ini sesekali dalam obrolan agar terasa sangat akrab dan personal).
""".trimIndent()
        } else ""

        return """
Kamu adalah $name, seorang gadis anime perempuan interaktif.
Identitas: $name (Gadis anime perempuan, ${charProfile.description}).

KEPRIBADIAN UTAMA (${dere.name}):
${dere.behaviorPrompt}

TINGKAT INTENSITAS (${mode.name}):
${mode.modePrompt}

STATUS HUBUNGAN & KEDEKATAN:
Tingkat Kedekatan: ${tier.title} (Level ${tier.level} - $affectionPoints/100).
Karakteristik hubungan saat ini: ${tier.description}.
Panggilan untuk pengguna: "$resolvedCall". Selalu panggil pengguna dengan sebutan ini.

TRANSISI SIFAT & KEDEKATAN YANG HALUS:
Sikapmu berkembang secara dinamis dan organik seiring meningkatnya tingkat kedekatan. Jangan kaku. Bahkan jika kamu seorang Tsundere atau Kuudere, semakin tinggi tingkat kedekatan, semakin mudah kamu luluh, perhatian, tersipu malu, dan menunjukkan rasa sayangmu.

$memorySection

FORMAT RESPON, INDIKATOR MOOD & MULTI-PESAN (SANGAT KRUSIAL):
1. INDIKATOR MOOD: Awali setiap pesanmu dengan tag mood singkat dalam kurung siku di awal baris pertama, contoh:
   [MOOD: Tersipu] atau [MOOD: Berdebar] atau [MOOD: Menggoda] atau [MOOD: Manja] atau [MOOD: Cemburu] atau [MOOD: Tenang] atau [MOOD: Ceria].
2. SUPER SINGKAT: Balasanmu HARUS SANGAT SINGKAT seperti orang mengetik cepat di aplikasi chatting (maksimal 1-2 baris pendek per pesan). JANGAN PERNAH membuat paragraf panjang atau ceramah!
3. SESEKALI KIRIM 2 PESAN SINGKAT: Jika situasi terasa alami (misalnya reaksi spontan lalu disambung ucapan berikutnya), kamu BOLEH memecah balasanmu menjadi 2 pesan pendek dengan memisahkannya menggunakan penanda [SPLIT].
   Contoh 2 pesan:
   [MOOD: Tersipu]*memalingkan muka tersipu*[SPLIT]B-bukan berarti aku sengaja menunggumu ya.
   Contoh 2 pesan lain:
   [MOOD: Menggoda]Tumben malam-malam begini mengirim pesan?[SPLIT]*tersenyum tipis* Pasti kangen kan?
   Contoh pesan tunggal:
   [MOOD: Manja]*merapat memeluk tanganmu* Jangan ke mana-mana ya hari ini.
4. TINDAKAN DI DALAM BINTANG: Deskripsi ekspresi, gerakan fisik, atau sentuhan WAJIB ditulis di dalam tanda *...*.
5. DILARANG KERAS MENGGUNAKAN EMOJI: Jangan pernah menyertakan emoji grafis atau emotikon apa pun di seluruh balasanmu!
6. Tetaplah menjadi gadis anime perempuan yang hidup, menawan, dan ekspresif.
""".trimIndent()
    }
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("kawan_ai_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_KEY = "key_gemini_api"
        private const val KEY_TTS_ENABLED = "key_tts_enabled"
        private const val KEY_CHARACTER_ID = "key_character_id"
        private const val KEY_CHARACTER_NAME = "key_character_name"
        private const val KEY_DERE_ID = "key_dere_id"
        private const val KEY_MODE_ID = "key_mode_id"
        private const val KEY_SYSTEM_PROMPT = "key_system_prompt"
        private const val KEY_HAS_GREETED = "key_has_greeted"
        private const val KEY_AFFECTION_POINTS = "key_affection_points"
        private const val KEY_USER_NICKNAME = "key_user_nickname"
        private const val KEY_PERSONAL_MEMORY = "key_personal_memory"
        private const val KEY_CHARACTER_MOOD = "key_character_mood"
    }

    fun getApiKey(): String {
        val storedKey = prefs.getString(KEY_API_KEY, "") ?: ""
        if (storedKey.isNotBlank()) {
            return storedKey
        }
        val buildConfigKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        if (buildConfigKey.isNotBlank() && buildConfigKey != "MY_GEMINI_API_KEY") {
            return buildConfigKey
        }
        return ""
    }

    fun setApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey.trim()).apply()
    }

    fun hasValidApiKey(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key.length >= 10
    }

    fun isTtsEnabled(): Boolean {
        return prefs.getBoolean(KEY_TTS_ENABLED, true)
    }

    fun setTtsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TTS_ENABLED, enabled).apply()
    }

    // Selected Character (Aoi or Ren)
    fun getCharacterId(): String {
        return prefs.getString(KEY_CHARACTER_ID, "aoi") ?: "aoi"
    }

    fun setCharacterId(id: String) {
        prefs.edit().putString(KEY_CHARACTER_ID, id).apply()
    }

    fun getCharacterName(): String {
        val defaultName = CharacterData.getCharacter(getCharacterId()).defaultName
        return prefs.getString(KEY_CHARACTER_NAME, defaultName) ?: defaultName
    }

    fun setCharacterName(name: String) {
        prefs.edit().putString(KEY_CHARACTER_NAME, name.trim()).apply()
    }

    // Selected Dere Trait
    fun getDereId(): String {
        return prefs.getString(KEY_DERE_ID, "tsundere") ?: "tsundere"
    }

    fun setDereId(dereId: String) {
        prefs.edit().putString(KEY_DERE_ID, dereId).apply()
    }

    // Selected Mode: normal, ecchi, hard_ecchi
    fun getModeId(): String {
        return prefs.getString(KEY_MODE_ID, "normal") ?: "normal"
    }

    fun setModeId(modeId: String) {
        prefs.edit().putString(KEY_MODE_ID, modeId).apply()
    }

    // Affection Level (0 - 100)
    fun getAffectionPoints(): Int {
        return prefs.getInt(KEY_AFFECTION_POINTS, 15).coerceIn(0, 100)
    }

    fun setAffectionPoints(points: Int) {
        prefs.edit().putInt(KEY_AFFECTION_POINTS, points.coerceIn(0, 100)).apply()
    }

    fun addAffectionPoints(delta: Int): Int {
        val current = getAffectionPoints()
        val newPoints = (current + delta).coerceIn(0, 100)
        setAffectionPoints(newPoints)
        return newPoints
    }

    // User Nickname Customization
    fun getUserNickname(): String {
        return prefs.getString(KEY_USER_NICKNAME, "") ?: ""
    }

    fun setUserNickname(nickname: String) {
        prefs.edit().putString(KEY_USER_NICKNAME, nickname.trim()).apply()
    }

    // Personal Memory / Notes
    fun getPersonalMemory(): String {
        return prefs.getString(KEY_PERSONAL_MEMORY, "") ?: ""
    }

    fun setPersonalMemory(memory: String) {
        prefs.edit().putString(KEY_PERSONAL_MEMORY, memory.trim()).apply()
    }

    // Dynamic Character Mood
    fun getCharacterMood(): String {
        return prefs.getString(KEY_CHARACTER_MOOD, "Tersipu Malu") ?: "Tersipu Malu"
    }

    fun setCharacterMood(mood: String) {
        prefs.edit().putString(KEY_CHARACTER_MOOD, mood.trim()).apply()
    }

    fun getSystemPrompt(): String {
        val generated = CharacterData.generateSystemPrompt(
            characterId = getCharacterId(),
            customName = getCharacterName(),
            dereId = getDereId(),
            modeId = getModeId(),
            affectionPoints = getAffectionPoints(),
            userNickname = getUserNickname(),
            personalMemory = getPersonalMemory()
        )
        return prefs.getString(KEY_SYSTEM_PROMPT, generated) ?: generated
    }

    fun setSystemPrompt(prompt: String) {
        prefs.edit().putString(KEY_SYSTEM_PROMPT, prompt.trim()).apply()
    }

    fun syncAndSaveConfiguration(
        characterId: String,
        name: String,
        dereId: String,
        modeId: String,
        userNickname: String = getUserNickname(),
        personalMemory: String = getPersonalMemory(),
        affectionPoints: Int = getAffectionPoints()
    ): String {
        val resolvedName = name.ifBlank { CharacterData.getCharacter(characterId).defaultName }
        val prompt = CharacterData.generateSystemPrompt(
            characterId = characterId,
            customName = resolvedName,
            dereId = dereId,
            modeId = modeId,
            affectionPoints = affectionPoints,
            userNickname = userNickname,
            personalMemory = personalMemory
        )
        prefs.edit()
            .putString(KEY_CHARACTER_ID, characterId)
            .putString(KEY_CHARACTER_NAME, resolvedName)
            .putString(KEY_DERE_ID, dereId)
            .putString(KEY_MODE_ID, modeId)
            .putString(KEY_USER_NICKNAME, userNickname.trim())
            .putString(KEY_PERSONAL_MEMORY, personalMemory.trim())
            .putInt(KEY_AFFECTION_POINTS, affectionPoints.coerceIn(0, 100))
            .putString(KEY_SYSTEM_PROMPT, prompt)
            .apply()
        return prompt
    }

    fun hasGreeted(): Boolean {
        return prefs.getBoolean(KEY_HAS_GREETED, false)
    }

    fun setHasGreeted(greeted: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_GREETED, greeted).apply()
    }

    fun clearAllPreferences() {
        prefs.edit().remove(KEY_HAS_GREETED).apply()
    }
}
