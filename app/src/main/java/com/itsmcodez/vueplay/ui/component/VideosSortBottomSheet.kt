package com.itsmcodez.vueplay.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.constant.PreferenceKeys
import com.itsmcodez.vueplay.extension.rememberPreference
import com.itsmcodez.vueplay.ui.component.vueui.VueBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideosSortBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: (SheetState) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(true)
    val (sortOrder, onSortOrderChanged) = context.rememberPreference(PreferenceKeys.VIDEOS_SORT_ORDER_KEY, PreferenceKeys.SortOrder.ASCENDING.name)
    val (sortOption, onSortOptionChanged) = context.rememberPreference(PreferenceKeys.VIDEOS_SORT_OPTION_KEY, PreferenceKeys.SortOption.NAME.name)
    val sortOrders = remember { PreferenceKeys.SortOrder.entries }
    val sortOptions = remember { PreferenceKeys.SortOption.entries }
    val sortOrderLabelsMap = remember(sortOrder, sortOption) {
        when(sortOption) {
            PreferenceKeys.SortOption.NAME.name -> mapOf(
                PreferenceKeys.SortOrder.ASCENDING to context.getString(R.string.sort_a_to_z),
                PreferenceKeys.SortOrder.DESCENDING to context.getString(R.string.sort_z_to_a)
            )

            PreferenceKeys.SortOption.DATE_ADDED.name -> mapOf(
                PreferenceKeys.SortOrder.ASCENDING to context.getString(R.string.sort_oldest),
                PreferenceKeys.SortOrder.DESCENDING to context.getString(R.string.sort_newest)
            )

            PreferenceKeys.SortOption.DURATION.name -> mapOf(
                PreferenceKeys.SortOrder.ASCENDING to context.getString(R.string.sort_shortest),
                PreferenceKeys.SortOrder.DESCENDING to context.getString(R.string.sort_longest)
            )

            else -> mapOf()
        }
    }

    VueBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismiss = onDismiss
    ) {
        Text(
            text = stringResource(R.string.title_videos_sort_bottom_sheet),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
        )

        sortOptions.fastForEach { option ->
            RadioButtonRow(
                selected = option.name == sortOption,
                onClick = { onSortOptionChanged(option.name) },
                label = option.label
            )
        }

        HorizontalDivider()

        sortOrders.fastForEach { order ->
            RadioButtonRow(
                selected = order.name == sortOrder,
                onClick = { onSortOrderChanged(order.name) },
                label = sortOrderLabelsMap[order] ?: order.label
            )
        }

        Button(
            modifier = Modifier.align(Alignment.End),
            contentPadding = PaddingValues(8.dp),
            onClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss(sheetState) } }
        ) {
            Text(text = stringResource(R.string.apply))
        }
    }
}