package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.ui.theme.VuePlayTheme

@Composable
fun SwitchButtonRow(
    modifier: Modifier = Modifier,
    checked: Boolean,
    enabled: Boolean = true,
    onClick: (() -> Unit)?,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    summary: String? = null
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
            Column(Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = label,
                    style = MaterialTheme.typography.titleMedium
                )
                if (summary != null) {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                }
            }
            Switch(
                modifier = Modifier.padding(horizontal = 4.dp),
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange,
                thumbContent = {
                    Icon(
                        painter = painterResource(
                            if(checked) R.drawable.rounded_check_small_24
                            else R.drawable.rounded_close_small_24
                        ),
                        contentDescription = label
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SwitchButtonRowPreview() {
    VuePlayTheme {
        SwitchButtonRow(
            checked = true,
            onClick = {},
            onCheckedChange = {},
            label = "Label",
            summary = "Summary"
        )
    }
}