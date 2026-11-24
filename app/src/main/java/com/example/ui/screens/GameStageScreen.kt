package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.game.model.ControlMode
import com.example.game.model.Speaker
import com.example.ui.components.DesktopKeyboardHintBar
import com.example.ui.components.SpiderSceneCanvas
import com.example.ui.components.TouchControlsOverlay
import com.example.ui.theme.CrispInkWhite
import com.example.ui.theme.DeepIndigoSurface
import com.example.ui.theme.ElectricWebCyan
import com.example.ui.theme.ElevatedCardIndigo
import com.example.ui.theme.EmeraldVictory
import com.example.ui.theme.GoldenSunset
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.MutedMoonlight
import com.example.ui.theme.OutlineIndigo
import com.example.ui.theme.RomanticQuoteStyle
import com.example.ui.theme.SpiderCrimson
import com.example.ui.theme.VelvetRose
import com.example.ui.viewmodel.GamePlayState
import kotlinx.coroutines.delay

@Composable
fun GameStageScreen(
    playState: GamePlayState,
    onMovePlayer: (Float, Float, Float) -> Unit,
    onWebAction: () -> Unit,
    onHotspotTap: (String) -> Unit,
    onAdvanceDialogue: () -> Unit,
    onSelectChoice: (Int) -> Unit,
    onManualSave: (Int?) -> Unit,
    onSetControlMode: (ControlMode) -> Unit,
    onToggleMute: () -> Unit,
    onDismissToast: () -> Unit,
    onExitToHub: () -> Unit
) {
    var showPauseDialog by remember { mutableStateOf(false) }
    var touchMoveX by remember { mutableFloatStateOf(0f) }
    var touchMoveY by remember { mutableFloatStateOf(0f) }
    var keyLeft by remember { mutableStateOf(false) }
    var keyRight by remember { mutableStateOf(false) }
    var keyUp by remember { mutableStateOf(false) }
    var keyDown by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
    }

    LaunchedEffect(playState.unlockedMemoryToast, playState.saveBanner) {
        if (playState.unlockedMemoryToast != null || playState.saveBanner != null) {
            delay(3200L)
            onDismissToast()
        }
    }

    BackHandler {
        showPauseDialog = !showPauseDialog
    }

    val combinedX = (if (keyRight) 1f else 0f) - (if (keyLeft) 1f else 0f) + touchMoveX
    val combinedY = (if (keyDown) 1f else 0f) - (if (keyUp) 1f else 0f) + touchMoveY

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                val isDown = event.type == KeyEventType.KeyDown
                val isUp = event.type == KeyEventType.KeyUp
                if (!isDown && !isUp) return@onPreviewKeyEvent false

                when (event.key) {
                    Key.A, Key.DirectionLeft -> {
                        keyLeft = isDown
                        true
                    }
                    Key.D, Key.DirectionRight -> {
                        keyRight = isDown
                        true
                    }
                    Key.W, Key.DirectionUp -> {
                        keyUp = isDown
                        true
                    }
                    Key.S, Key.DirectionDown -> {
                        keyDown = isDown
                        true
                    }
                    Key.Spacebar, Key.Enter -> {
                        if (isDown) {
                            if (playState.dialogueActive) onAdvanceDialogue() else onWebAction()
                        }
                        true
                    }
                    Key.One -> {
                        if (isDown && playState.dialogueActive) onSelectChoice(0)
                        true
                    }
                    Key.Two -> {
                        if (isDown && playState.dialogueActive) onSelectChoice(1)
                        true
                    }
                    Key.Three -> {
                        if (isDown && playState.dialogueActive) onSelectChoice(2)
                        true
                    }
                    Key.Escape, Key.P -> {
                        if (isDown) showPauseDialog = !showPauseDialog
                        true
                    }
                    else -> false
                }
            }
    ) {
        val isWideScreen = maxWidth >= 820.dp
        val showTouchControls = when (playState.controlMode) {
            ControlMode.TOUCH -> true
            ControlMode.KEYBOARD -> false
            ControlMode.AUTO -> !isWideScreen
        }

        // Interactive 60fps Canvas Scene
        SpiderSceneCanvas(
            playState = playState,
            moveInputX = combinedX.coerceIn(-1f, 1f),
            moveInputY = combinedY.coerceIn(-1f, 1f),
            onStepMovement = onMovePlayer,
            onHotspotTap = onHotspotTap,
            modifier = Modifier.fillMaxSize()
        )

        // Safe-area HUD + Controls overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Game HUD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MidnightNavy.copy(alpha = 0.84f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.border(1.dp, playState.chapter.accentColor, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                            Text(
                                text = "CAPÍTULO ${playState.chapter.number}: ${playState.chapter.title.uppercase()}",
                                style = MaterialTheme.typography.titleMedium,
                                color = playState.chapter.accentColor
                            )
                            Text(
                                text = "${playState.completedHotspotIds.size}/${playState.chapter.hotspots.size} objetivos  •  ${playState.chapter.locationName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CrispInkWhite
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MidnightNavy.copy(alpha = 0.84f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.border(1.dp, VelvetRose, RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Vínculo Canónico",
                                    tint = VelvetRose,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${playState.activeSlot?.bondScore ?: 50}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = GoldenSunset
                                )
                            }
                        }

                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MidnightNavy.copy(alpha = 0.82f))
                                .testTag("mute_button")
                        ) {
                            Icon(
                                imageVector = if (playState.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (playState.isMuted) "Activar sonido" else "Silenciar",
                                tint = CrispInkWhite
                            )
                        }

                        IconButton(
                            onClick = { showPauseDialog = true },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SpiderCrimson)
                                .testTag("pause_save_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pausa y Guardado",
                                tint = CrispInkWhite
                            )
                        }
                    }
                }

                // Objective & Reaction Banner
                val progress = if (playState.chapter.hotspots.isEmpty()) 1f else {
                    playState.completedHotspotIds.size.toFloat() / playState.chapter.hotspots.size.toFloat()
                }
                Surface(
                    color = MidnightNavy.copy(alpha = 0.78f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.widthIn(max = 580.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = playState.reactionBanner ?: playState.chapter.objectiveLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrispInkWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            color = playState.chapter.accentColor,
                            trackColor = OutlineIndigo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }

                // Toasts for Cloud Save or Memory Unlocked
                AnimatedVisibility(
                    visible = playState.unlockedMemoryToast != null || playState.saveBanner != null,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = ElevatedCardIndigo.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.border(1.5.dp, EmeraldVictory, RoundedCornerShape(14.dp))
                    ) {
                        val toastText = playState.unlockedMemoryToast?.let {
                            "Recuerdo desbloqueado para Isabella: «${it.title}»"
                        } ?: playState.saveBanner.orEmpty()
                        Text(
                            text = toastText,
                            style = MaterialTheme.typography.labelLarge,
                            color = EmeraldVictory,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Bottom Interactive Area: Either Canonical Dialogue Overlay OR Adaptive Controls
            if (playState.dialogueActive) {
                CanonicalDialogueCard(
                    playState = playState,
                    onAdvance = onAdvanceDialogue,
                    onSelectChoice = onSelectChoice,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            } else {
                if (showTouchControls) {
                    val remainingDone = playState.completedHotspotIds.size >= playState.chapter.hotspots.size
                    TouchControlsOverlay(
                        onDirectionChanged = { dx, dy ->
                            touchMoveX = dx
                            touchMoveY = dy
                        },
                        onWebAction = onWebAction,
                        actionLabel = if (remainingDone) "HABLAR" else "¡THWIP!"
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        DesktopKeyboardHintBar()
                    }
                }
            }
        }
    }

    if (showPauseDialog) {
        PauseAndSaveDialog(
            playState = playState,
            onDismiss = { showPauseDialog = false },
            onSaveCurrent = { onManualSave(null) },
            onSaveToSlot = { slotNum -> onManualSave(slotNum) },
            onControlModeSelected = onSetControlMode,
            onExitToHub = {
                showPauseDialog = false
                onExitToHub()
            }
        )
    }
}

@Composable
private fun CanonicalDialogueCard(
    playState: GamePlayState,
    onAdvance: () -> Unit,
    onSelectChoice: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentBeat = playState.chapter.dialogueBeats.getOrNull(playState.dialogueBeatIndex)
    val pending = playState.pendingChoiceResponse

    val speaker = pending?.first ?: currentBeat?.speaker ?: Speaker.NARRADOR
    val bodyText = pending?.second ?: currentBeat?.text.orEmpty()
    val choices = if (pending == null) currentBeat?.choices.orEmpty() else emptyList()

    Surface(
        color = DeepIndigoSurface.copy(alpha = 0.94f),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp,
        modifier = modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth()
            .border(2.dp, ElectricWebCyan.copy(alpha = 0.75f), RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val badgeColor = when (speaker) {
                    Speaker.SPIDER_MAN -> SpiderCrimson
                    Speaker.ISABELLA -> VelvetRose
                    Speaker.NARRADOR -> GoldenSunset
                }
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = speaker.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (speaker == Speaker.NARRADOR) MidnightNavy else CrispInkWhite,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = bodyText,
                style = if (speaker == Speaker.ISABELLA) RomanticQuoteStyle else MaterialTheme.typography.bodyLarge,
                color = CrispInkWhite
            )

            if (choices.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    choices.forEachIndexed { idx, choice ->
                        Surface(
                            color = ElevatedCardIndigo,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, OutlineIndigo, RoundedCornerShape(14.dp))
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { onSelectChoice(idx) }
                                .testTag("dialogue_choice_${choice.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${idx + 1}. ${choice.text}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CrispInkWhite,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = choice.traitTag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldenSunset
                                )
                            }
                        }
                    }
                }
            } else {
                Button(
                    onClick = onAdvance,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpiderCrimson),
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(48.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .testTag("dialogue_continue_button")
                ) {
                    Text(
                        text = if (pending != null || playState.dialogueBeatIndex >= playState.chapter.dialogueBeats.lastIndex) {
                            if (playState.chapter.number >= 5) "Ver Epílogo Canónico" else "Siguiente Capítulo"
                        } else {
                            "Continuar"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PauseAndSaveDialog(
    playState: GamePlayState,
    onDismiss: () -> Unit,
    onSaveCurrent: () -> Unit,
    onSaveToSlot: (Int) -> Unit,
    onControlModeSelected: (ControlMode) -> Unit,
    onExitToHub: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepIndigoSurface,
        titleContentColor = CrispInkWhite,
        textContentColor = MutedMoonlight,
        title = {
            Text(
                text = "PAUSA Y GUARDADO",
                style = MaterialTheme.typography.headlineMedium,
                color = CrispInkWhite
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ranura activa: ${playState.activeSlot?.slotName ?: "Ranura 1"}")

                Button(
                    onClick = onSaveCurrent,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpiderCrimson),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("manual_save_button")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Guardar Progreso en la Nube")
                }

                Text("Copiar guardado a otra ranura:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..3).forEach { slotNum ->
                        OutlinedButton(
                            onClick = { onSaveToSlot(slotNum) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("R$slotNum", color = ElectricWebCyan)
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.SportsEsports, contentDescription = null, tint = GoldenSunset, modifier = Modifier.size(18.dp))
                    Text("Modo de Controles:", style = MaterialTheme.typography.labelLarge, color = CrispInkWhite)
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ControlMode.entries.forEach { mode ->
                        val selected = playState.controlMode == mode
                        Surface(
                            color = if (selected) ElevatedCardIndigo else MidnightNavy,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (selected) ElectricWebCyan else OutlineIndigo,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onControlModeSelected(mode) }
                        ) {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) ElectricWebCyan else CrispInkWhite,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = onExitToHub,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("exit_to_hub_button")
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Salir al Menú Principal", color = CrispInkWhite)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Seguir Jugando", color = ElectricWebCyan)
            }
        }
    )
}
