package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.itsmcodez.vueplay.R

val speedMultipliers = listOf(
    0.5f to "0.5x",
    1f to "1x (Normal)",
    1.5f to "1.5x",
    2f to "2x"
)

@Composable
fun SpeedMultiplierDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    currentSpeed: Float
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_dialog_speed_multiplier)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                speedMultipliers.forEach {
                    RadioButtonRow(
                        label = it.second,
                        selected = it.first == currentSpeed,
                        onClick = { onSpeedChange(it.first) }
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.dismiss)) }}
    )
}
