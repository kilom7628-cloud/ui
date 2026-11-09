package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AlphaState
import com.example.ui.theme.BorderDark
import com.example.ui.theme.ElevatedDark
import com.example.ui.theme.GrayMuted
import com.example.ui.theme.GraySubtle
import com.example.ui.theme.PureWhite

@Composable
fun WelcomeScreen(
    onSelectPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.7f))

        // Official AHADU Logo Icon (from user-provided image)
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, BorderDark, RoundedCornerShape(24.dp))
                .background(ElevatedDark),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ahadu_official_logo),
                contentDescription = "AHADU Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(96.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Brand Title
        Text(
            text = "AHADU",
            fontFamily = FontFamily.SansSerif,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = PureWhite
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "AETHER INDUSTRIES // ALPHA NEURAL ARCHITECTURE",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            color = GrayMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "An intelligent conversational neural assistant engineered by Aether Industries.",
            style = MaterialTheme.typography.bodyLarge,
            color = GraySubtle,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
