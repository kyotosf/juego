package com.example.game.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class ControlMode(val label: String) {
    AUTO("Automático (Móvil / PC)"),
    TOUCH("Táctil en pantalla"),
    KEYBOARD("Teclado PC (WASD / Flechas)")
}

enum class SceneType {
    WEB_SWING_ROOFTOPS,
    ROOFTOP_TERRACE_EXPLORE,
    BRIDGE_DRONE_RESCUE,
    MEMORY_GALLERY_REVEAL,
    STARLIGHT_CONSTELLATION
}

enum class Speaker(val displayName: String) {
    SPIDER_MAN("Spider-Man (Peter)"),
    ISABELLA("Isabella"),
    NARRADOR("Crónica Canónica")
}

enum class HotspotKind {
    ROSE,
    SKETCHBOOK,
    LANTERN,
    VINYL,
    HOT_CHOCOLATE,
    DRONE,
    RELIC_CAMERA,
    RELIC_SCARF,
    RELIC_TICKET,
    RELIC_LETTER,
    CONSTELLATION_STAR
}

data class WorldHotspot(
    val id: String,
    val label: String,
    val xFraction: Float,
    val yFraction: Float,
    val kind: HotspotKind,
    val unlocksMemoryId: String? = null,
    val reactionText: String
)

data class StoryChoice(
    val id: String,
    val text: String,
    val traitTag: String,
    val bondDelta: Int,
    val courageDelta: Int,
    val tendernessDelta: Int,
    val witDelta: Int,
    val responseSpeaker: Speaker,
    val responseText: String,
    val unlocksMemoryId: String? = null
)

data class DialogueBeat(
    val speaker: Speaker,
    val text: String,
    val choices: List<StoryChoice> = emptyList()
)

data class CanonicalMemory(
    val id: String,
    val title: String,
    val chapterTag: String,
    val quote: String,
    val letterBody: String,
    val kind: HotspotKind
)

data class ChapterSpec(
    val number: Int,
    val title: String,
    val subtitle: String,
    val locationName: String,
    @DrawableRes val backdropResId: Int,
    val accentColor: Color,
    val sceneType: SceneType,
    val objectiveLabel: String,
    val hotspots: List<WorldHotspot>,
    val dialogueBeats: List<DialogueBeat>,
    val completionMemoryId: String
)
