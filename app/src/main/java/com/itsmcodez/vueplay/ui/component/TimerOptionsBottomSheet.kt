package com.itsmcodez.vueplay.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.itsmcodez.vueplay.App
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.ui.component.vueui.VueBottomSheet
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

val options = listOf(
    App.instance.getString(R.string.off) to 0.minutes,
    App.instance.getString(R.string.timer_option_5_mins) to 5.minutes,
    App.instance.getString(R.string.timer_option_10_mins) to 10.minutes,
    App.instance.getString(R.string.timer_option_15_mins) to 15.minutes,
    App.instance.getString(R.string.timer_option_30_mins) to 30.minutes,
    App.instance.getString(R.string.timer_option_45_mins) to 45.minutes,
    App.instance.getString(R.string.timer_option_1_hour) to 1.hours,
    App.instance.getString(R.string.timer_option_2_hours) to 2.hours
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerOptionsBottomSheet(
    modifier: Modifier = Modifier,
    currentTimerOption: Long,
    onOptionSelected: (option: Long) -> Unit,
    onDismiss: (SheetState?) -> Unit
) {
    VueBottomSheet(
        modifier = modifier,
        onDismiss = onDismiss
    ) {
        Text(
            text = stringResource(R.string.title_timer_options_bottom_sheet),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
        )

        options.forEach {
            RadioButtonRow(
                label = it.first,
                selected = it.second.inWholeMilliseconds == currentTimerOption,
                onClick = { onOptionSelected(it.second.inWholeMilliseconds); onDismiss(null) }
            )
        }
    }
}