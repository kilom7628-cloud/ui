package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.model.AlphaState
import com.example.ui.theme.BorderDark
import com.example.ui.theme.ElevatedDark
import com.example.ui.theme.GrayLight
import com.example.ui.theme.PureWhite

/**
 * Clean monochrome status indicator pill for ALPHA states
 */
@Composable
fun AlphaStatusIndicator(
    state: AlphaState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state != AlphaState.IDLE,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        val label = when (state) {
            AlphaState.THINKING -> "ALPHA: THINKING..."
            AlphaState.SEARCHING -> "AETHER MESH: SEARCHING..."
            AlphaState.PROCESSING -> "TENSOR CORE: PROCESSING..."
            AlphaState.RESPONDING -> "ALPHA: STREAMING..."
            AlphaState.ERROR -> "SYSTEM NOTICE"
            AlphaState.IDLE -> ""
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(ElevatedDark.copy(alpha = 0.95f))
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlphaEnergyVisual(
                    state = state,
                    size = 16.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayLight
                )
            }
        }
    }
}
