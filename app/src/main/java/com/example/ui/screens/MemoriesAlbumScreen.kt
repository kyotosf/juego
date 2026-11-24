package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.game.model.CanonicalMemory
import com.example.game.model.StoryCatalog
import com.example.ui.theme.CrispInkWhite
import com.example.ui.theme.DeepIndigoSurface
import com.example.ui.theme.ElectricWebCyan
import com.example.ui.theme.ElevatedCardIndigo
import com.example.ui.theme.GoldenSunset
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.MutedMoonlight
import com.example.ui.theme.OutlineIndigo
import com.example.ui.theme.RomanticQuoteStyle
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.VelvetRose
import com.example.ui.theme.WarmParchment

@Composable
fun MemoriesAlbumScreen(
    unlockedMemoryIds: Set<String>,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val memories = StoryCatalog.memories
    var selectedMemory by remember(memories, unlockedMemoryIds) {
        mutableStateOf(
            memories.firstOrNull { it.id in unlockedMemoryIds } ?: memories.first()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("memories_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al menú",
                        tint = CrispInkWhite
                    )
                }
                Column {
                    Text(
                        text = "ÁLBUM DE ISABELLA",
                        style = MaterialTheme.typography.displayMedium,
                        color = CrispInkWhite
                    )
                    Text(
                        text = "${unlockedMemoryIds.size} de ${memories.size} cartas y recuerdos desbloqueados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldenSunset
                    )
                }
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpanded = maxWidth >= 720.dp
            if (isExpanded) {
                // Computer / Tablet Canonical List-Detail Split Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(memories, key = { it.id }) { mem ->
                            val unlocked = mem.id in unlockedMemoryIds
                            MemoryCardItem(
                                memory = mem,
                                unlocked = unlocked,
                                selected = selectedMemory.id == mem.id,
                                onClick = { selectedMemory = mem }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxHeight()
                    ) {
                        LoveLetterReaderPane(
                            memory = selectedMemory,
                            unlocked = selectedMemory.id in unlockedMemoryIds
                        )
                    }
                }
            } else {
                // Mobile Layout: Reader on top + Grid below
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LoveLetterReaderPane(
                        memory = selectedMemory,
                        unlocked = selectedMemory.id in unlockedMemoryIds,
                        modifier = Modifier.fillMaxWidth()
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(memories, key = { it.id }) { mem ->
                            MemoryCardItem(
                                memory = mem,
                                unlocked = mem.id in unlockedMemoryIds,
                                selected = selectedMemory.id == mem.id,
                                onClick = { selectedMemory = mem }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryCardItem(
    memory: CanonicalMemory,
    unlocked: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        selected -> ElectricWebCyan
        unlocked -> VelvetRose.copy(alpha = 0.6f)
        else -> OutlineIndigo
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) ElevatedCardIndigo else DeepIndigoSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = onClick)
            .testTag("memory_card_${memory.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = memory.chapterTag,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (unlocked) GoldenSunset else MutedMoonlight
                )
                Icon(
                    imageVector = if (unlocked) Icons.Default.Favorite else Icons.Default.Lock,
                    contentDescription = if (unlocked) "Recuerdo desbloqueado" else "Recuerdo bloqueado",
                    tint = if (unlocked) VelvetRose else MutedMoonlight,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (unlocked) memory.title else "Recuerdo Bloqueado",
                style = MaterialTheme.typography.titleMedium,
                color = if (unlocked) CrispInkWhite else MutedMoonlight
            )
        }
    }
}

@Composable
private fun LoveLetterReaderPane(
    memory: CanonicalMemory,
    unlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (unlocked) WarmParchment else DeepIndigoSurface,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 6.dp,
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (unlocked) VelvetRose else OutlineIndigo,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(22.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (unlocked) Icons.Default.MailOutline else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (unlocked) VelvetRose else MutedMoonlight
                )
                Text(
                    text = if (unlocked) "${memory.chapterTag} • Carta para Isabella" else "Aún por descubrir",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (unlocked) VelvetRose else MutedMoonlight
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (unlocked) {
                Text(
                    text = memory.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MidnightNavy
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = memory.quote,
                    style = RomanticQuoteStyle,
                    color = ColorForQuote
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = memory.letterBody,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MidnightNavy.copy(alpha = 0.88f)
                )
            } else {
                Text(
                    text = "Explora ${memory.chapterTag} e interactúa con sus objetos brillantes o toma decisiones románticas para desbloquear esta carta secreta.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftBlush.copy(alpha = 0.8f)
                )
            }
        }
    }
}

private val ColorForQuote = MidnightNavy.copy(alpha = 0.72f)
