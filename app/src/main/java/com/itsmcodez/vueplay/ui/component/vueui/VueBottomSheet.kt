package com.itsmcodez.vueplay.ui.component.vueui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object VueBottomSheetDefaults {
    enum class MaxHeight(val value: Dp) {
        Small(320.dp),
        Medium(600.dp),
        Fill(0.dp)
    }

    fun maxHeight() = MaxHeight.Medium
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VueBottomSheet(
    modifier: Modifier = Modifier,
    skipPartiallyExpanded: Boolean = true,
    maxHeight: VueBottomSheetDefaults.MaxHeight = VueBottomSheetDefaults.maxHeight(),
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded),
    onDismiss: (SheetState) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onDismiss(sheetState) },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VueSpacing.lg)
                .verticalScroll(rememberScrollState())
                .then(
                    if (maxHeight == VueBottomSheetDefaults.MaxHeight.Fill)
                        Modifier.fillMaxHeight()
                    else
                        Modifier.heightIn(max = maxHeight.value)
                ),
            content = content
        )
    }
}