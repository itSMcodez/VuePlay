package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
fun NavigableSectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    scrollToFirstEnabled: Boolean = true,
    scrollToLastEnabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    onScrollToFirstItem: (() -> Unit)? = null,
    onScrollToLastItem: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick.invoke() }
                else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            icon?.let {
                Box(Modifier.size(32.dp)) { it() }
            }

            Column(Modifier.weight(1f)) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee(),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1
                )
                subtitle?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee(),
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f),
                        maxLines = 1
                    )
                }
            }

            onScrollToFirstItem?.let {
                IconButton(onClick = it, enabled = scrollToFirstEnabled) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_chevron_backward_24),
                        contentDescription = null
                    )
                }
            }

            onScrollToLastItem?.let {
                IconButton(onClick = it, enabled = scrollToLastEnabled) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_chevron_forward_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NavigableSectionHeaderPreview() {
    VuePlayTheme {
        NavigableSectionHeader(
            title = "Trending",
            subtitle = "Movies and Tv Series",
            onScrollToFirstItem = {},
            onScrollToLastItem = {},
            icon = {
                Image(
                    painter = painterResource(R.drawable.trending),
                    contentDescription = null
                )
            }
        )
    }
}
