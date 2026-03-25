package com.wardenprotocol.game.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wardenprotocol.game.ui.theme.*

@Composable
fun MainMenuScreen(
    highScore: Int,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vault_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            for (i in 1..5) {
                val radius = 50f * i
                drawCircle(
                    color = VaultGreen.copy(alpha = pulseAlpha * (0.6f - i * 0.1f)),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
            }
            
            drawCircle(
                color = VaultGreen.copy(alpha = pulseAlpha),
                radius = 15f,
                center = Offset(centerX, centerY)
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "THE WARDEN PROTOCOL",
                style = MaterialTheme.typography.displayLarge,
                color = VaultGreen
            )
            
            Text(
                text = "The last light of humanity sleeps below. You are its guardian.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onNewGame,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Text(
                    text = "NEW MISSION",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            if (highScore > 0) {
                Text(
                    text = "HIGH SCORE: $highScore",
                    style = MaterialTheme.typography.titleLarge,
                    color = VaultGreen
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
