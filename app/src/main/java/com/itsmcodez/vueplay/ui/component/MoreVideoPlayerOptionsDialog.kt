package com.itsmcodez.vueplay.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.util.TimeFormatter

@Composable
fun MoreVideoPlayerOptionsDialog(
    modifier: Modifier = Modifier,
    isTimerRunning: Boolean,
    onDismiss: () -> Unit,
    onUpdateRemainingTimer: () -> Long,
    onTimerClick: () -> Unit,
    onEqualizerClick: () -> Unit
) {
    val remainingTimerMs by rememberUpdatedState(onUpdateRemainingTimer())
    val isCountDownTimerRunning = remember(isTimerRunning, remainingTimerMs) {
        isTimerRunning && remainingTimerMs != 0L
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background.copy(0.85f))
                .padding(8.dp)
        ) {
            FlowRow(
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                VideoPlayerOptionsGridItem(
                    icon = R.drawable.rounded_timer_24,
                    label = R.string.timer,
                    onClick = onTimerClick,
                    iconTint = if (isCountDownTimerRunning) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                    labelTint = if (isCountDownTimerRunning) MaterialTheme.colorScheme.primary else LocalContentColor.current
                ) {
                    if (isCountDownTimerRunning)
                        Text(
                            text = TimeFormatter.formatTimeMilliseconds(
                                locale = LocalLocale.current.platformLocale,
                                milliseconds = remainingTimerMs
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall
                        )
                }

                VideoPlayerOptionsGridItem(
                    icon = R.drawable.rounded_equalizer_24,
                    label = R.string.equalizer,
                    onClick = onEqualizerClick
                )

            }
        }
    }
}

@Composable
fun VideoPlayerOptionsGridItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes label: Int,
    iconTint: Color = LocalContentColor.current,
    labelTint: Color = Color.Unspecified,
    onClick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(icon),
                contentDescription = stringResource(label),
                tint = iconTint
            )
        }
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall,
            color = labelTint
        )
        content()
    }
}
