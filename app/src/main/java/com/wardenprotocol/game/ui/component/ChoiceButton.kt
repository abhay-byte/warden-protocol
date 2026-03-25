package com.wardenprotocol.game.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wardenprotocol.game.ui.theme.SignalCyan

@Composable
fun ChoiceButton(
    label: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ActionButton(
        title = label,
        subtitle = description,
        icon = Icons.Filled.PlayArrow,
        accent = SignalCyan,
        onClick = onClick,
        modifier = modifier
    )
}
