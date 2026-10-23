package com.itsmcodez.vueplay.ui.component.vueui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.ui.theme.VuePlayTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.milliseconds

/**
 * Vue UI text field wrapping [TextField].
 */
@OptIn(FlowPreview::class)
@Composable
fun VueTextField(
    modifier: Modifier = Modifier,
    onValueChange: (CharSequence) -> Unit,
    state: TextFieldState,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    labelPosition: TextFieldLabelPosition = TextFieldLabelPosition.Attached(),
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
    shape: Shape = RoundedCornerShape(16.dp),
    colors: TextFieldColors = TextFieldDefaults.colors().copy(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        cursorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
    ),
    contentPadding: PaddingValues =
        if (label == null || labelPosition is TextFieldLabelPosition.Above) {
            TextFieldDefaults.contentPaddingWithoutLabel(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            )
        } else {
            TextFieldDefaults.contentPaddingWithLabel(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            )
        },
    interactionSource: MutableInteractionSource? = null,
) {
    LaunchedEffect(state) {
        snapshotFlow { state.text }
            .debounce(500.milliseconds)
            .collectLatest { value -> onValueChange(value.trim()) }
    }

    TextField(
        modifier = modifier.heightIn(min = 40.dp),
        state = state,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        labelPosition = labelPosition,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        onTextLayout = onTextLayout,
        scrollState = scrollState,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    )
}

@Composable
@Preview(showBackground = true)
fun VueTextFieldPreview() {
    VuePlayTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VueTextField(
                label = { Text(text = "Test") },
                state = TextFieldState("This is a test"),
                onValueChange = {}
            )
        }
    }
}
