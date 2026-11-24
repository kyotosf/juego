package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CrispInkWhite
import com.example.ui.theme.ElectricWebCyan
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.MutedMoonlight
import com.example.ui.theme.OutlineIndigo
import com.example.ui.theme.SpiderCrimson

@Composable
fun TouchControlsOverlay(
    onDirectionChanged: (Float, Float) -> Unit,
    onWebAction: () -> Unit,
    actionLabel: String,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Ergonomic Cross D-Pad (min 48dp per button)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DpadHoldButton(
                icon = Icons.Default.KeyboardArrowUp,
                description = "Mover arriba",
                testTag = "dpad_up",
                onPressChange = { pressed ->
                    onDirectionChanged(0f, if (pressed) -1f else 0f)
                }
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DpadHoldButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    description = "Mover izquierda",
                    testTag = "dpad_left",
                    onPressChange = { pressed ->
                        onDirectionChanged(if (pressed) -1f else 0f, 0f)
                    }
                )
                DpadHoldButton(
                    icon = Icons.Default.KeyboardArrowDown,
                    description = "Mover abajo",
                    testTag = "dpad_down",
                    onPressChange = { pressed ->
                        onDirectionChanged(0f, if (pressed) 1f else 0f)
                    }
                )
                DpadHoldButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    description = "Mover derecha",
                    testTag = "dpad_right",
                    onPressChange = { pressed ->
                        onDirectionChanged(if (pressed) 1f else 0f, 0f)
                    }
                )
            }
        }

        // Primary Web Action Button
        Button(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onWebAction()
            },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SpiderCrimson,
                contentColor = CrispInkWhite
            ),
            modifier = Modifier
                .size(width = 148.dp, height = 58.dp)
                .border(2.dp, ElectricWebCyan, RoundedCornerShape(24.dp))
                .pointerHoverIcon(PointerIcon.Hand)
                .testTag("action_thwip_button")
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = ElectricWebCyan,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun DpadHoldButton(
    icon: ImageVector,
    description: String,
    testTag: String,
    onPressChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(MidnightNavy.copy(alpha = 0.78f))
            .border(1.5.dp, OutlineIndigo, CircleShape)
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPressChange(true)
                        try {
                            awaitRelease()
                        } finally {
                            onPressChange(false)
                        }
                    }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = CrispInkWhite
        )
    }
}

@Composable
fun DesktopKeyboardHintBar(
    modifier: Modifier = Modifier
) {
    Surface(
        color = MidnightNavy.copy(alpha = 0.84f),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        modifier = modifier.border(1.dp, OutlineIndigo, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Keyboard,
                contentDescription = "Controles de computadora",
                tint = ElectricWebCyan
            )
            Text(
                text = "WASD / Flechas: Moverse   •   Espacio / Clic: ¡THWIP! / Interactuar   •   1-3: Elegir   •   Esc: Pausa / Guardar",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedMoonlight
            )
        }
    }
}
