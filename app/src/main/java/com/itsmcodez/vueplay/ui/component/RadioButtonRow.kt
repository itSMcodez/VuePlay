package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtonRow(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: (() -> Unit)?,
    label: String
) {
    Box(
        modifier = modifier.clickable {
            if (onClick != null) onClick()
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                modifier = Modifier.padding(horizontal = 4.dp),
                selected = selected,
                onClick = onClick
            )

            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}