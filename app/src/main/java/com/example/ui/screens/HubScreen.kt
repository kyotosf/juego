package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.SaveSlot
import com.example.game.model.StoryCatalog
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
import com.example.ui.viewmodel.UiState

@Composable
fun HubScreen(
    userName: String,
    isGuestMode: Boolean,
    slotsState: UiState<List<SaveSlot>>,
    isSaving: Boolean,
    errorBanner: String?,
    onStartNewGame: (Int, String) -> Unit,
    onContinueSlot: (SaveSlot, Int?) -> Unit,
    onDeleteSlot: (String) -> Unit,
    onOpenMemories: (Set<String>) -> Unit,
    onSignOut: () -> Unit,
    onDismissError: () -> Unit
) {
    var showNewGameDialogForSlot by remember { mutableIntStateOf(0) }
    var newGameSlotName by remember { mutableStateOf("") }
    var deleteConfirmSlot by remember { mutableStateOf<SaveSlot?>(null) }

    val allSlots = (slotsState as? UiState.Success)?.data ?: emptyList()
    val latestSlot = allSlots.maxByOrNull { it.updatedAt?.seconds ?: 0L }
    val combinedMemories = remember(allSlots) {
        allSlots.flatMap { it.unlockedMemories }.toSet()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpanded = maxWidth >= 820.dp

            if (isExpanded) {
                // Computer / Tablet Adaptive Two-Column Canonical Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HeroDedicationBanner(
                            userName = userName,
                            isGuestMode = isGuestMode,
                            latestSlot = latestSlot,
                            memoryCount = combinedMemories.size,
                            onContinueLatest = {
                                if (latestSlot != null) {
                                    onContinueSlot(latestSlot, null)
                                } else {
                                    newGameSlotName = "Historia de Isabella"
                                    showNewGameDialogForSlot = 1
                                }
                            },
                            onOpenMemories = { onOpenMemories(combinedMemories) },
                            onSignOut = onSignOut
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SaveSlotsPanel(
                            slotsState = slotsState,
                            isSaving = isSaving,
                            onPlaySlot = { slot, ch -> onContinueSlot(slot, ch) },
                            onCreateSlot = { slotNum ->
                                newGameSlotName = "Historia de Isabella — Ranura $slotNum"
                                showNewGameDialogForSlot = slotNum
                            },
                            onRequestDelete = { deleteConfirmSlot = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Mobile Vertical Layout
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        HeroDedicationBanner(
                            userName = userName,
                            isGuestMode = isGuestMode,
                            latestSlot = latestSlot,
                            memoryCount = combinedMemories.size,
                            onContinueLatest = {
                                if (latestSlot != null) {
                                    onContinueSlot(latestSlot, null)
                                } else {
                                    newGameSlotName = "Historia de Isabella"
                                    showNewGameDialogForSlot = 1
                                }
                            },
                            onOpenMemories = { onOpenMemories(combinedMemories) },
                            onSignOut = onSignOut
                        )
                    }
                    item {
                        SaveSlotsPanel(
                            slotsState = slotsState,
                            isSaving = isSaving,
                            onPlaySlot = { slot, ch -> onContinueSlot(slot, ch) },
                            onCreateSlot = { slotNum ->
                                newGameSlotName = "Historia de Isabella — Ranura $slotNum"
                                showNewGameDialogForSlot = slotNum
                            },
                            onRequestDelete = { deleteConfirmSlot = it }
                        )
                    }
                }
            }
        }
    }

    if (showNewGameDialogForSlot in 1..3) {
        AlertDialog(
            onDismissRequest = { showNewGameDialogForSlot = 0 },
            containerColor = DeepIndigoSurface,
            titleContentColor = CrispInkWhite,
            textContentColor = MutedMoonlight,
            title = { Text("Nueva Partida • Ranura $showNewGameDialogForSlot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Elige un nombre para guardar esta aventura en tu nube:")
                    OutlinedTextField(
                        value = newGameSlotName,
                        onValueChange = { if (it.length <= 50) newGameSlotName = it },
                        singleLine = true,
                        label = { Text("Nombre de partida") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_slot_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val slotNum = showNewGameDialogForSlot
                        showNewGameDialogForSlot = 0
                        onStartNewGame(slotNum, newGameSlotName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpiderCrimson),
                    modifier = Modifier.testTag("confirm_new_slot_button")
                ) {
                    Text("Comenzar Cap. 1")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewGameDialogForSlot = 0 }) {
                    Text("Cancelar", color = MutedMoonlight)
                }
            }
        )
    }

    deleteConfirmSlot?.let { slot ->
        AlertDialog(
            onDismissRequest = { deleteConfirmSlot = null },
            containerColor = DeepIndigoSurface,
            titleContentColor = CrispInkWhite,
            textContentColor = MutedMoonlight,
            title = { Text("¿Borrar Ranura ${slot.slotNumber}?") },
            text = { Text("Se eliminará el progreso guardado de «${slot.slotName}» en la nube.") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteConfirmSlot = null
                        onDeleteSlot(slot.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpiderCrimson)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmSlot = null }) {
                    Text("Cancelar", color = MutedMoonlight)
                }
            }
        )
    }

    if (errorBanner != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            containerColor = DeepIndigoSurface,
            titleContentColor = CrispInkWhite,
            textContentColor = MutedMoonlight,
            title = { Text("Aviso de Guardado") },
            text = { Text(errorBanner) },
            confirmButton = {
                TextButton(onClick = onDismissError) {
                    Text("Entendido", color = ElectricWebCyan)
                }
            }
        )
    }
}

@Composable
private fun HeroDedicationBanner(
    userName: String,
    isGuestMode: Boolean,
    latestSlot: SaveSlot?,
    memoryCount: Int,
    onContinueLatest: () -> Unit,
    onOpenMemories: () -> Unit,
    onSignOut: () -> Unit
) {
    Surface(
        color = DeepIndigoSurface,
        shape = RoundedCornerShape(26.dp),
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, OutlineIndigo, RoundedCornerShape(26.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_rooftop_sunset_1795533350764),
                    contentDescription = "Panorama de Nueva York al atardecer",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MidnightNavy.copy(alpha = 0.25f),
                                    DeepIndigoSurface.copy(alpha = 0.96f)
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MidnightNavy.copy(alpha = 0.78f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.border(1.dp, ElectricWebCyan.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isGuestMode) Icons.Default.PersonOutline else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (isGuestMode) ElectricWebCyan else EmeraldVictory,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isGuestMode) {
                                    "Invitado • Guardado local"
                                } else {
                                    userName.ifBlank { "Isabella" }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = CrispInkWhite
                            )
                        }
                    }

                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MidnightNavy.copy(alpha = 0.75f))
                            .testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.sign_out),
                            tint = CrispInkWhite
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "SPIDER-LOVE",
                        style = MaterialTheme.typography.displayLarge,
                        color = CrispInkWhite
                    )
                    Text(
                        text = "Historia Canónica para Isabella",
                        style = RomanticQuoteStyle,
                        color = GoldenSunset
                    )
                }
            }

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onContinueLatest,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpiderCrimson,
                        contentColor = CrispInkWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .testTag("hero_continue_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (latestSlot != null) {
                            "Continuar • Cap. ${latestSlot.currentChapter} (${latestSlot.bondScore}% Vínculo)"
                        } else {
                            "Comenzar Historia Canónica"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = onOpenMemories,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .testTag("open_memories_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = VelvetRose,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Álbum de Cartas y Recuerdos ($memoryCount/10)",
                        color = CrispInkWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveSlotsPanel(
    slotsState: UiState<List<SaveSlot>>,
    isSaving: Boolean,
    onPlaySlot: (SaveSlot, Int?) -> Unit,
    onCreateSlot: (Int) -> Unit,
    onRequestDelete: (SaveSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RANURAS DE GUARDADO",
                style = MaterialTheme.typography.headlineMedium,
                color = CrispInkWhite
            )
            if (isSaving) {
                CircularProgressIndicator(
                    color = ElectricWebCyan,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        when (slotsState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricWebCyan)
                }
            }
            is UiState.Error -> {
                Surface(
                    color = ElevatedCardIndigo,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = slotsState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is UiState.Success -> {
                val byNum = slotsState.data.associateBy { it.slotNumber.toInt() }
                (1..3).forEach { slotNum ->
                    val slot = byNum[slotNum]
                    if (slot != null) {
                        OccupiedSlotCard(
                            slot = slot,
                            onPlay = { ch -> onPlaySlot(slot, ch) },
                            onOverwrite = { onCreateSlot(slotNum) },
                            onDelete = { onRequestDelete(slot) }
                        )
                    } else {
                        EmptySlotCard(
                            slotNumber = slotNum,
                            onCreate = { onCreateSlot(slotNum) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OccupiedSlotCard(
    slot: SaveSlot,
    onPlay: (Int?) -> Unit,
    onOverwrite: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ElevatedCardIndigo),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, OutlineIndigo, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = slot.slotName,
                        style = MaterialTheme.typography.titleMedium,
                        color = CrispInkWhite
                    )
                    Text(
                        text = "${slot.lastChapterTitle}  •  ${slot.unlockedMemories.size}/10 recuerdos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedMoonlight
                    )
                }

                Surface(
                    color = MidnightNavy,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = VelvetRose,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${slot.bondScore}%",
                            style = MaterialTheme.typography.labelLarge,
                            color = GoldenSunset
                        )
                    }
                }
            }

            // Quick Chapter Selector pills up to unlocked chapter
            val maxUnlocked = if (slot.finished) 5 else slot.currentChapter.toInt().coerceIn(1, 5)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items((1..maxUnlocked).toList()) { chNum ->
                    val ch = StoryCatalog.chapterByNumber(chNum)
                    Surface(
                        color = MidnightNavy.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .border(1.dp, ch.accentColor.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { onPlay(chNum) }
                    ) {
                        Text(
                            text = "Cap. $chNum: ${ch.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrispInkWhite,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onPlay(null) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpiderCrimson),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .testTag("play_slot_${slot.slotNumber}")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (slot.finished) "Rejugar Cap. ${slot.currentChapter}" else "Cargar Partida")
                }

                IconButton(
                    onClick = onOverwrite,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MidnightNavy)
                        .testTag("reset_slot_${slot.slotNumber}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reiniciar ranura",
                        tint = ElectricWebCyan
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MidnightNavy)
                        .testTag("delete_slot_${slot.slotNumber}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar ranura",
                        tint = MutedMoonlight
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySlotCard(
    slotNumber: Int,
    onCreate: () -> Unit
) {
    Surface(
        color = DeepIndigoSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineIndigo, RoundedCornerShape(20.dp))
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = onCreate)
            .testTag("empty_slot_$slotNumber")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Ranura $slotNumber — Disponible",
                    style = MaterialTheme.typography.titleMedium,
                    color = CrispInkWhite
                )
                Text(
                    text = "Toca para comenzar una nueva historia canónica",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedMoonlight
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ElevatedCardIndigo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear partida",
                    tint = ElectricWebCyan
                )
            }
        }
    }
}
