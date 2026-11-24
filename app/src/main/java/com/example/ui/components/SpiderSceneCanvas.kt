package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.HotspotKind
import com.example.game.model.SceneType
import com.example.game.model.WorldHotspot
import com.example.ui.theme.CrispInkWhite
import com.example.ui.theme.ElectricWebCyan
import com.example.ui.theme.GoldenSunset
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SpiderCrimson
import com.example.ui.theme.VelvetRose
import com.example.ui.viewmodel.GamePlayState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private class ParticlePool(count: Int) {
    val x = FloatArray(count)
    val y = FloatArray(count)
    val speedY = FloatArray(count)
    val speedX = FloatArray(count)
    val size = FloatArray(count)
    val phase = FloatArray(count)

    init {
        val rng = Random(2026)
        for (i in 0 until count) {
            x[i] = rng.nextFloat()
            y[i] = rng.nextFloat()
            speedY[i] = 0.04f + rng.nextFloat() * 0.14f
            speedX[i] = (rng.nextFloat() - 0.5f) * 0.04f
            size[i] = 4f + rng.nextFloat() * 9f
            phase[i] = rng.nextFloat() * (2f * PI.toFloat())
        }
    }

    fun step(dt: Float, sceneType: SceneType) {
        val count = x.size
        for (i in 0 until count) {
            when (sceneType) {
                SceneType.BRIDGE_DRONE_RESCUE, SceneType.MEMORY_GALLERY_REVEAL -> {
                    y[i] += (0.45f + speedY[i] * 2.2f) * dt
                    x[i] -= 0.12f * dt
                }
                SceneType.STARLIGHT_CONSTELLATION -> {
                    y[i] -= (speedY[i] * 0.35f) * dt
                    x[i] += sin(phase[i] + y[i] * 6f) * 0.015f * dt
                }
                else -> {
                    y[i] += speedY[i] * dt
                    x[i] += (speedX[i] + sin(phase[i] + y[i] * 8f) * 0.02f) * dt
                }
            }
            if (y[i] > 1.05f) y[i] = -0.05f
            if (y[i] < -0.05f) y[i] = 1.05f
            if (x[i] > 1.05f) x[i] = -0.05f
            if (x[i] < -0.05f) x[i] = 1.05f
        }
    }
}

@Composable
fun SpiderSceneCanvas(
    playState: GamePlayState,
    moveInputX: Float,
    moveInputY: Float,
    onStepMovement: (Float, Float, Float) -> Unit,
    onHotspotTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val particles = remember { ParticlePool(34) }
    var elapsedTime by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(playState.chapter.number, playState.dialogueActive) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                if (lastNanos != 0L) {
                    val dt = ((frameNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.033f)
                    elapsedTime += dt
                    particles.step(dt, playState.chapter.sceneType)
                    if (!playState.dialogueActive && (moveInputX != 0f || moveInputY != 0f)) {
                        onStepMovement(moveInputX, moveInputY, dt)
                    }
                }
                lastNanos = frameNanos
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val heightPx = with(LocalDensity.current) { maxHeight.toPx() }
        val parallaxOffsetX = (0.5f - playState.playerX) * 28f

        // Layer 1: High-resolution chapter artwork with smooth parallax
        Image(
            painter = painterResource(id = playState.chapter.backdropResId),
            contentDescription = playState.chapter.locationName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.08f
                    scaleY = 1.08f
                    translationX = parallaxOffsetX
                }
        )

        // Layer 2: Cinematic gradient & atmospheric tint
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MidnightNavy.copy(alpha = 0.38f),
                            Color.Transparent,
                            MidnightNavy.copy(alpha = 0.72f)
                        )
                    )
                )
        )

        // Layer 3: Custom 60fps Canvas graphics (particles, constellation silk, web physics, characters)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Subtle Spider-Verse comic halftone edge dots
            drawHalftoneCorner(w, h, playState.chapter.accentColor)

            // Scene-specific atmospheric particles
            drawAtmosphericParticles(particles, elapsedTime, playState.chapter.sceneType, w, h)

            // Chapter 5: Connect completed constellation stars with golden-cyan spider silk!
            if (playState.chapter.sceneType == SceneType.STARLIGHT_CONSTELLATION) {
                drawConstellationLinks(
                    hotspots = playState.chapter.hotspots,
                    completed = playState.completedHotspotIds,
                    elapsedTime = elapsedTime,
                    w = w,
                    h = h
                )
            }

            // Draw remaining and completed hotspot glows
            playState.chapter.hotspots.forEachIndexed { idx, hotspot ->
                val done = hotspot.id in playState.completedHotspotIds
                val cx = hotspot.xFraction * w
                val cy = hotspot.yFraction * h
                val bob = sin(elapsedTime * 2.6f + idx * 1.1f) * 6f
                drawHotspotNode(
                    hotspot = hotspot,
                    center = Offset(cx, cy + bob),
                    completed = done,
                    pulse = (sin(elapsedTime * 4f + idx) + 1f) * 0.5f
                )
            }

            // Draw Isabella waiting gracefully on the right side of the scenario
            val isabellaPos = Offset(w * 0.89f, h * 0.67f)
            drawIsabellaCharacter(
                center = isabellaPos,
                elapsedTime = elapsedTime,
                accentColor = playState.chapter.accentColor
            )

            // Draw active web silk shot ("¡THWIP!")
            val spiderPos = Offset(playState.playerX * w, playState.playerY * h)
            if (playState.webActive) {
                val targetPos = Offset(playState.webTargetX * w, playState.webTargetY * h)
                drawWebSilkLine(spiderPos, targetPos, elapsedTime)
            }

            // Draw Spider-Man character
            drawSpiderManCharacter(
                center = spiderPos,
                facingRight = playState.facingRight,
                isMoving = playState.isMoving,
                elapsedTime = elapsedTime
            )
        }

        // Layer 4: Interactive 48dp+ clickable & mouse-hoverable Hotspot Targets
        playState.chapter.hotspots.forEach { hotspot ->
            val isDone = hotspot.id in playState.completedHotspotIds
            if (!isDone) {
                HotspotOverlayButton(
                    hotspot = hotspot,
                    containerWidthPx = widthPx,
                    containerHeightPx = heightPx,
                    accentColor = playState.chapter.accentColor,
                    onClick = { onHotspotTap(hotspot.id) }
                )
            }
        }
    }
}

@Composable
private fun HotspotOverlayButton(
    hotspot: WorldHotspot,
    containerWidthPx: Float,
    containerHeightPx: Float,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val density = LocalDensity.current
    val hitSizeDp = 64.dp
    val hitHalfPx = with(density) { (hitSizeDp / 2).toPx() }
    val xPx = (hotspot.xFraction * containerWidthPx - hitHalfPx).roundToInt()
    val yPx = (hotspot.yFraction * containerHeightPx - hitHalfPx).roundToInt()

    Box(
        modifier = Modifier
            .offset { IntOffset(xPx, yPx) }
            .size(hitSizeDp)
            .clip(CircleShape)
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .semantics { contentDescription = "Interactuar con ${hotspot.label}" }
            .testTag("hotspot_${hotspot.id}"),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            color = if (isHovered) accentColor else MidnightNavy.copy(alpha = 0.85f),
            shape = RoundedCornerShape(10.dp),
            tonalElevation = 4.dp,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Text(
                text = hotspot.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isHovered) MidnightNavy else CrispInkWhite,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

private fun DrawScope.drawHalftoneCorner(w: Float, h: Float, accent: Color) {
    val dotColor = accent.copy(alpha = 0.14f)
    val step = 28f
    for (row in 0..4) {
        for (col in 0..5) {
            val radius = (5f - row * 0.8f).coerceAtLeast(1.5f)
            drawCircle(
                color = dotColor,
                radius = radius,
                center = Offset(24f + col * step, 24f + row * step)
            )
            drawCircle(
                color = dotColor,
                radius = radius,
                center = Offset(w - 24f - col * step, h - 24f - row * step)
            )
        }
    }
}

private fun DrawScope.drawAtmosphericParticles(
    pool: ParticlePool,
    time: Float,
    sceneType: SceneType,
    w: Float,
    h: Float
) {
    for (i in pool.x.indices) {
        val px = pool.x[i] * w
        val py = pool.y[i] * h
        val sz = pool.size[i]
        when (sceneType) {
            SceneType.BRIDGE_DRONE_RESCUE, SceneType.MEMORY_GALLERY_REVEAL -> {
                val color = if (i % 2 == 0) ElectricWebCyan.copy(alpha = 0.38f) else VelvetRose.copy(alpha = 0.30f)
                drawLine(
                    color = color,
                    start = Offset(px, py),
                    end = Offset(px - 6f, py + sz * 2.4f),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
            SceneType.STARLIGHT_CONSTELLATION -> {
                val glow = 0.45f + 0.4f * sin(time * 2.5f + pool.phase[i])
                drawCircle(
                    color = GoldenSunset.copy(alpha = glow * 0.3f),
                    radius = sz * 1.9f,
                    center = Offset(px, py)
                )
                drawRoundRect(
                    color = GoldenSunset.copy(alpha = glow),
                    topLeft = Offset(px - sz * 0.45f, py - sz * 0.65f),
                    size = Size(sz * 0.9f, sz * 1.3f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
            else -> {
                // Romantic falling rose petals & golden fireflies
                rotate(degrees = (time * 35f + i * 25f) % 360f, pivot = Offset(px, py)) {
                    drawOval(
                        color = if (i % 3 == 0) GoldenSunset.copy(alpha = 0.65f) else VelvetRose.copy(alpha = 0.72f),
                        topLeft = Offset(px - sz * 0.6f, py - sz * 0.35f),
                        size = Size(sz * 1.2f, sz * 0.7f)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawConstellationLinks(
    hotspots: List<WorldHotspot>,
    completed: Set<String>,
    elapsedTime: Float,
    w: Float,
    h: Float
) {
    for (i in 0 until hotspots.size - 1) {
        val a = hotspots[i]
        val b = hotspots[i + 1]
        if (a.id in completed && b.id in completed) {
            val start = Offset(a.xFraction * w, a.yFraction * h)
            val end = Offset(b.xFraction * w, b.yFraction * h)
            drawLine(
                color = GoldenSunset.copy(alpha = 0.45f + 0.25f * sin(elapsedTime * 3f)),
                start = start,
                end = end,
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = CrispInkWhite,
                start = start,
                end = end,
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawHotspotNode(
    hotspot: WorldHotspot,
    center: Offset,
    completed: Boolean,
    pulse: Float
) {
    if (completed) {
        drawCircle(
            color = GoldenSunset.copy(alpha = 0.28f),
            radius = 16f,
            center = center
        )
        drawCircle(
            color = CrispInkWhite.copy(alpha = 0.85f),
            radius = 5f,
            center = center
        )
        return
    }

    val ringColor = when (hotspot.kind) {
        HotspotKind.DRONE -> ElectricWebCyan
        HotspotKind.CONSTELLATION_STAR -> GoldenSunset
        else -> VelvetRose
    }

    // Outer pulsing aura
    drawCircle(
        color = ringColor.copy(alpha = 0.22f + pulse * 0.25f),
        radius = 28f + pulse * 10f,
        center = center
    )
    drawCircle(
        color = ringColor,
        radius = 20f,
        center = center,
        style = Stroke(width = 3f)
    )
    drawCircle(
        color = MidnightNavy.copy(alpha = 0.82f),
        radius = 18f,
        center = center
    )

    when (hotspot.kind) {
        HotspotKind.ROSE -> {
            drawCircle(SpiderCrimson, radius = 9f, center = center)
            drawCircle(VelvetRose, radius = 5f, center = Offset(center.x - 2f, center.y - 2f))
        }
        HotspotKind.DRONE -> {
            drawCircle(ElectricWebCyan, radius = 8f, center = center)
            drawLine(CrispInkWhite, Offset(center.x - 13f, center.y), Offset(center.x + 13f, center.y), strokeWidth = 2.5f)
            drawLine(CrispInkWhite, Offset(center.x, center.y - 13f), Offset(center.x, center.y + 13f), strokeWidth = 2.5f)
        }
        HotspotKind.CONSTELLATION_STAR -> {
            val starPath = Path().apply {
                moveTo(center.x, center.y - 13f)
                lineTo(center.x + 4f, center.y - 4f)
                lineTo(center.x + 13f, center.y)
                lineTo(center.x + 4f, center.y + 4f)
                lineTo(center.x, center.y + 13f)
                lineTo(center.x - 4f, center.y + 4f)
                lineTo(center.x - 13f, center.y)
                lineTo(center.x - 4f, center.y - 4f)
                close()
            }
            drawPath(starPath, color = GoldenSunset)
        }
        else -> {
            drawCircle(GoldenSunset, radius = 8f, center = center)
            drawCircle(CrispInkWhite, radius = 3.5f, center = center)
        }
    }
}

private fun DrawScope.drawWebSilkLine(start: Offset, end: Offset, time: Float) {
    drawLine(
        color = ElectricWebCyan.copy(alpha = 0.45f),
        start = start,
        end = end,
        strokeWidth = 9f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = CrispInkWhite,
        start = start,
        end = end,
        strokeWidth = 3.5f,
        cap = StrokeCap.Round
    )
    val burstRadius = 14f + (sin(time * 25f) + 1f) * 6f
    drawCircle(
        color = ElectricWebCyan.copy(alpha = 0.5f),
        radius = burstRadius,
        center = end,
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawSpiderManCharacter(
    center: Offset,
    facingRight: Boolean,
    isMoving: Boolean,
    elapsedTime: Float
) {
    val bob = if (isMoving) sin(elapsedTime * 14f) * 4f else sin(elapsedTime * 3.2f) * 2.5f
    val leanDeg = if (isMoving) (if (facingRight) 10f else -10f) else 0f
    val pivot = Offset(center.x, center.y + bob)

    // Soft cyan/crimson hero aura
    drawCircle(
        color = SpiderCrimson.copy(alpha = 0.22f),
        radius = 38f,
        center = pivot
    )

    rotate(degrees = leanDeg, pivot = pivot) {
        // Suit torso (midnight blue sides + crimson chest)
        drawRoundRect(
            color = Color(0xFF102A71),
            topLeft = Offset(pivot.x - 14f, pivot.y - 6f),
            size = Size(28f, 30f),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawRoundRect(
            color = SpiderCrimson,
            topLeft = Offset(pivot.x - 10f, pivot.y - 6f),
            size = Size(20f, 22f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Mask head
        val headCenter = Offset(pivot.x, pivot.y - 18f)
        drawCircle(color = SpiderCrimson, radius = 15f, center = headCenter)
        drawCircle(color = MidnightNavy, radius = 15f, center = headCenter, style = Stroke(width = 2f))

        // Expressive Spider-Man white comic eyes
        val dir = if (facingRight) 1f else -1f
        val eyeCenter = Offset(headCenter.x + dir * 4f, headCenter.y - 1f)
        val eyePath = Path().apply {
            moveTo(eyeCenter.x - 6f * dir, eyeCenter.y - 4f)
            lineTo(eyeCenter.x + 7f * dir, eyeCenter.y - 6f)
            lineTo(eyeCenter.x + 5f * dir, eyeCenter.y + 4f)
            close()
        }
        drawPath(eyePath, color = MidnightNavy)
        val innerEye = Path().apply {
            moveTo(eyeCenter.x - 4f * dir, eyeCenter.y - 2.5f)
            lineTo(eyeCenter.x + 5f * dir, eyeCenter.y - 4f)
            lineTo(eyeCenter.x + 3.5f * dir, eyeCenter.y + 2.5f)
            close()
        }
        drawPath(innerEye, color = CrispInkWhite)

        // Web-shooter wrist accent
        drawCircle(
            color = ElectricWebCyan,
            radius = 3.5f,
            center = Offset(pivot.x + dir * 15f, pivot.y + 6f)
        )
    }
}

private fun DrawScope.drawIsabellaCharacter(
    center: Offset,
    elapsedTime: Float,
    accentColor: Color
) {
    val sway = sin(elapsedTime * 2.2f) * 3f
    val pos = Offset(center.x, center.y + sway)

    // Romantic glowing heart halo behind Isabella
    drawCircle(
        color = VelvetRose.copy(alpha = 0.22f),
        radius = 36f,
        center = pos
    )

    // Flowing hair silhouette
    drawOval(
        color = Color(0xFF2B1726),
        topLeft = Offset(pos.x - 16f, pos.y - 31f),
        size = Size(32f, 34f)
    )
    // Coat / dress with crimson scarf detail
    drawRoundRect(
        color = Color(0xFF251D4A),
        topLeft = Offset(pos.x - 13f, pos.y - 4f),
        size = Size(26f, 32f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = VelvetRose,
        topLeft = Offset(pos.x - 12f, pos.y - 6f),
        size = Size(24f, 8f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Face
    drawCircle(
        color = Color(0xFFFFD8C4),
        radius = 12f,
        center = Offset(pos.x, pos.y - 16f)
    )

    // Floating heart above Isabella
    val heartY = pos.y - 40f + sin(elapsedTime * 3.5f) * 4f
    val heartPath = Path().apply {
        moveTo(pos.x, heartY + 6f)
        cubicTo(pos.x - 9f, heartY - 2f, pos.x - 5f, heartY - 9f, pos.x, heartY - 4f)
        cubicTo(pos.x + 5f, heartY - 9f, pos.x + 9f, heartY - 2f, pos.x, heartY + 6f)
        close()
    }
    drawPath(heartPath, color = accentColor)
}
