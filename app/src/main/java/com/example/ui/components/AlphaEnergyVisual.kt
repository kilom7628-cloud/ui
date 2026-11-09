package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.AlphaState
import com.example.ui.theme.GrayMuted
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureWhite
import kotlin.math.cos
import kotlin.math.sin

/**
 * Minimalist monochrome digital energy visualization for ALPHA Neural Core.
 * Pure system white-and-black contrast.
 */
@Composable
fun AlphaEnergyVisual(
    state: AlphaState,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "alpha_energy_mono")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = when (state) {
                AlphaState.THINKING -> 3000
                AlphaState.SEARCHING -> 2200
                AlphaState.PROCESSING -> 1800
                AlphaState.RESPONDING -> 3600
                else -> 8000
            }, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val primaryColor = PureWhite
    val secondaryColor = GrayMuted

    val particleCount = 6
    val particleOffsets = remember {
        (0 until particleCount).map { i -> (i * (360f / particleCount)) }
    }

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val baseRadius = (this.size.minDimension / 2f) * 0.75f

        // 1. Clean radial monochrome field
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = if (state == AlphaState.IDLE) 0.12f else 0.28f),
                    secondaryColor.copy(alpha = 0.04f),
                    Color.Transparent
                ),
                center = center,
                radius = baseRadius * 1.5f * pulse
            ),
            center = center,
            radius = baseRadius * 1.5f * pulse
        )

        // 2. Inner geometric core
        val hexPath = Path()
        val hexRadius = baseRadius * 0.5f * (if (state != AlphaState.IDLE) pulse else 1f)
        for (i in 0 until 6) {
            val angleRad = Math.toRadians((rotation + (i * 60.0)).toDouble())
            val x = center.x + (hexRadius * cos(angleRad)).toFloat()
            val y = center.y + (hexRadius * sin(angleRad)).toFloat()
            if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
        }
        hexPath.close()

        drawPath(
            path = hexPath,
            color = primaryColor.copy(alpha = 0.9f),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // 3. Central singularity
        drawCircle(
            color = primaryColor,
            radius = 3.dp.toPx() * (if (state != AlphaState.IDLE) pulse else 1f),
            center = center
        )

        // 4. Orbiting particles in crisp white
        particleOffsets.forEachIndexed { index, baseAngle ->
            val angle = baseAngle + if (index % 2 == 0) rotation else -rotation * 1.2f
            val rad = Math.toRadians(angle.toDouble())
            val distance = baseRadius * (0.85f + (index % 2) * 0.15f) * (if (state != AlphaState.IDLE) pulse else 1f)
            val px = center.x + (distance * cos(rad)).toFloat()
            val py = center.y + (distance * sin(rad)).toFloat()

            drawCircle(
                color = if (index % 2 == 0) PureWhite else OffWhite.copy(alpha = 0.8f),
                radius = 1.8.dp.toPx(),
                center = Offset(px, py)
            )

            if (state != AlphaState.IDLE && index % 2 == 0) {
                drawLine(
                    color = PureWhite.copy(alpha = 0.25f),
                    start = center,
                    end = Offset(px, py),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}
