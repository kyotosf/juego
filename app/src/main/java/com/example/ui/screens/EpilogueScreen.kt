package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.SaveSlot
import com.example.game.model.StoryCatalog
import com.example.ui.theme.CrispInkWhite
import com.example.ui.theme.DeepIndigoSurface
import com.example.ui.theme.ElectricWebCyan
import com.example.ui.theme.GoldenSunset
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.OutlineIndigo
import com.example.ui.theme.RomanticQuoteStyle
import com.example.ui.theme.SpiderCrimson
import com.example.ui.theme.VelvetRose
import com.example.ui.theme.WarmParchment

@Composable
fun EpilogueScreen(
    slot: SaveSlot,
    onOpenMemories: () -> Unit,
    onReturnToHub: () -> Unit
) {
    BackHandler(onBack = onReturnToHub)

    val (dominantTrait, letterBody) = StoryCatalog.epilogueDedication(
        bondScore = slot.bondScore.toInt(),
        courage = slot.couragePoints.toInt(),
        tenderness = slot.tendernessPoints.toInt(),
        wit = slot.witPoints.toInt()
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_starlight_sanctuary_1795533352110),
            contentDescription = "Observatorio estelar de Isabella",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MidnightNavy.copy(alpha = 0.5f),
                            MidnightNavy.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = DeepIndigoSurface.copy(alpha = 0.92f),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .border(2.dp, GoldenSunset, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(26.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenSunset)
                        Text(
                            text = "FINAL CANÓNICO DESBLOQUEADO",
                            style = MaterialTheme.typography.displayMedium,
                            color = GoldenSunset,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = dominantTrait,
                        style = RomanticQuoteStyle,
                        color = VelvetRose,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge("Vínculo", "${slot.bondScore}%")
                        StatBadge("Valentía", "${slot.couragePoints}")
                        StatBadge("Ternura", "${slot.tendernessPoints}")
                        StatBadge("Complicidad", "${slot.witPoints}")
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Dedication parchment card
                    Surface(
                        color = WarmParchment,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, VelvetRose, RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = letterBody,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MidnightNavy,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenMemories,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("epilogue_memories_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = VelvetRose,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text("Álbum (${slot.unlockedMemories.size}/10)", color = CrispInkWhite)
                        }

                        Button(
                            onClick = onReturnToHub,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SpiderCrimson,
                                contentColor = CrispInkWhite
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("epilogue_hub_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = ElectricWebCyan,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text("Volver al Menú")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String) {
    Surface(
        color = MidnightNavy.copy(alpha = 0.7f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.border(1.dp, OutlineIndigo, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = ElectricWebCyan)
            Text(label, style = MaterialTheme.typography.labelSmall, color = CrispInkWhite)
        }
    }
}
