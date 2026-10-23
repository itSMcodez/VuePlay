package com.itsmcodez.vueplay.ui.component.vueui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.ui.theme.VuePlayTheme

enum class VueSemanticButtonType {
    INFO,
    WARN,
    SUCCESS,
    ERROR
}

enum class VueButtonState {
    LOADING,
    DISABLED,
    NORMAL
}

sealed class VueButtonStyle {
    object Default : VueButtonStyle()
    object Tonal : VueButtonStyle()
    data class Elevated(val elevation: ButtonElevation) : VueButtonStyle()
    object Text : VueButtonStyle()
    data class Outlined(var outlineColor: Color) : VueButtonStyle()
    data class Stateful(val currentState: VueButtonState = VueButtonState.NORMAL) : VueButtonStyle()
}

object VueButtonDefaults {

    @Composable
    private fun calculateSemanticButtonContainerColor(
        style: VueButtonStyle,
        type: VueSemanticButtonType
    ): Color {
        return when(style) {
            is VueButtonStyle.Outlined -> {
                when(type) {
                    VueSemanticButtonType.INFO -> style.outlineColor = VueColors.Semantics.Info.copy(0.5f)
                    VueSemanticButtonType.WARN -> style.outlineColor = VueColors.Semantics.Warning.copy(0.5f)
                    VueSemanticButtonType.SUCCESS -> style.outlineColor = VueColors.Semantics.Success.copy(0.5f)
                    VueSemanticButtonType.ERROR -> style.outlineColor = VueColors.materialColorScheme().error.copy(0.5f)
                }
                Color.Transparent
            }

            is VueButtonStyle.Text -> Color.Transparent

            else -> {
                when(type) {
                    VueSemanticButtonType.INFO -> VueColors.Semantics.InfoContainer
                    VueSemanticButtonType.WARN -> VueColors.Semantics.WarningContainer
                    VueSemanticButtonType.SUCCESS -> VueColors.Semantics.SuccessContainer
                    VueSemanticButtonType.ERROR -> VueColors.materialColorScheme().errorContainer
                }
            }
        }
    }

    @Composable
    private fun calculateSemanticButtonContentColor(type: VueSemanticButtonType, style: VueButtonStyle): Color {
        return when(type) {
            VueSemanticButtonType.INFO -> when(style) {
                is VueButtonStyle.Outlined -> VueColors.Semantics.Info
                is VueButtonStyle.Text -> VueColors.Semantics.Info
                else -> VueColors.Semantics.InfoOnContainer
            }

            VueSemanticButtonType.WARN -> when(style) {
                is VueButtonStyle.Outlined -> VueColors.Semantics.Warning
                is VueButtonStyle.Text -> VueColors.Semantics.Warning
                else -> VueColors.Semantics.WarningOnContainer
            }

            VueSemanticButtonType.SUCCESS -> when(style) {
                is VueButtonStyle.Outlined -> VueColors.Semantics.Success
                is VueButtonStyle.Text -> VueColors.Semantics.Success
                else -> VueColors.Semantics.SuccessOnContainer
            }

            VueSemanticButtonType.ERROR -> when(style) {
                is VueButtonStyle.Outlined -> VueColors.materialColorScheme().error
                is VueButtonStyle.Text -> VueColors.materialColorScheme().error
                else -> VueColors.materialColorScheme().onErrorContainer
            }
        }
    }

    @Composable
    fun semanticButtonColors(style: VueButtonStyle, type: VueSemanticButtonType) = ButtonDefaults.buttonColors().copy(
        containerColor = calculateSemanticButtonContainerColor(style = style, type = type),
        contentColor = calculateSemanticButtonContentColor(type = type, style = style)
    )

    @Composable
    private fun calculateButtonContainerColor(style: VueButtonStyle): Color = when(style) {
        is VueButtonStyle.Outlined, VueButtonStyle.Text -> { Color.Transparent }
        is VueButtonStyle.Tonal -> { VueColors.materialColorScheme().surfaceContainer }
        else -> { VueColors.materialColorScheme().inverseSurface }
    }

    @Composable
    private fun calculateButtonContentColor(style: VueButtonStyle): Color = when(style) {
        is VueButtonStyle.Outlined, VueButtonStyle.Text, VueButtonStyle.Tonal -> { VueColors.materialColorScheme().onSurface }
        else -> { VueColors.materialColorScheme().inverseOnSurface }
    }

    @Composable
    fun buttonColors(style: VueButtonStyle) = ButtonDefaults.buttonColors().copy(
        containerColor = calculateButtonContainerColor(style),
        contentColor = calculateButtonContentColor(style)
    )

    object Style {
        @Composable
        fun default() = VueButtonStyle.Default
        @Composable
        fun tonal() = VueButtonStyle.Tonal
        @Composable
        fun elevated(
            elevation: ButtonElevation = ButtonDefaults.elevatedButtonElevation()
        ) = VueButtonStyle.Elevated(elevation = elevation)
        @Composable
        fun text() = VueButtonStyle.Text
        @Composable
        fun outlined(
            outlineColor: Color = VueColors.materialColorScheme().outline
        ) = VueButtonStyle.Outlined(outlineColor = outlineColor)
        @Composable
        fun stateful(
            currentState: VueButtonState = VueButtonState.NORMAL
        ) = VueButtonStyle.Stateful(currentState = currentState)
    }
}


@Composable
fun VueButton(
    modifier: Modifier = Modifier,
    style: VueButtonStyle = VueButtonStyle.Default,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = VueButtonDefaults.buttonColors(style),
    elevation: ButtonElevation? = if(style is VueButtonStyle.Elevated) style.elevation else ButtonDefaults.buttonElevation(),
    border: BorderStroke? = if (style is VueButtonStyle.Outlined) ButtonDefaults.outlinedButtonBorder() else null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        when(style) {
            is VueButtonStyle.Outlined -> {
                OutlinedButton(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    colors = colors,
                    elevation = elevation,
                    border = border,
                    contentPadding = contentPadding,
                    interactionSource = interactionSource,
                    content = content
                )
            }

            is VueButtonStyle.Text -> {
                TextButton(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    colors = colors,
                    elevation = elevation,
                    border = border,
                    contentPadding = contentPadding,
                    interactionSource = interactionSource,
                    content = content
                )
            }

            else -> {
                Button(
                    onClick = {
                        if(style is VueButtonStyle.Stateful) {
                            if (style.currentState == VueButtonState.NORMAL)
                                onClick()
                        } else onClick()
                    },
                    colors = colors,
                    modifier = modifier,
                    enabled = if(style is VueButtonStyle.Stateful) {
                        if(style.currentState == VueButtonState.DISABLED) false
                        else enabled
                    } else enabled,
                    shape = shape,
                    elevation = elevation,
                    border = border,
                    contentPadding = contentPadding,
                    interactionSource = interactionSource,
                    content = content
                )
            }
        }

        if(style is VueButtonStyle.Stateful) {
            when(style.currentState) {
                VueButtonState.LOADING -> {
                    LoadingIndicatorOverlay(
                        modifier = Modifier.matchParentSize(),
                        shape = shape,
                        color = colors.contentColor
                    )
                }
                else -> { }
            }
        }
    }
}

@Composable
fun VueSemanticButton(
    modifier: Modifier = Modifier,
    type: VueSemanticButtonType,
    style: VueButtonStyle = VueButtonStyle.Default,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = VueButtonDefaults.semanticButtonColors(style = style, type = type),
    elevation: ButtonElevation? = if(style is VueButtonStyle.Elevated) style.elevation else ButtonDefaults.buttonElevation(),
    border: BorderStroke? = if (style is VueButtonStyle.Outlined) ButtonDefaults.outlinedButtonBorder() else null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        when(style) {
            is VueButtonStyle.Outlined -> {
                OutlinedButton(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    colors = colors,
                    elevation = elevation,
                    border = border,
                    contentPadding = contentPadding,
                    interactionSource = interactionSource,
                    content = content
                )
            }

            is VueButtonStyle.Text -> {
                TextButton(
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    shape = shape,
                    colors = colors,
                    elevation = elevation,
                    border = border,
                    contentPadding = contentPadding,
                    interactionSource = interactionSource,
                    content = content
                )
            }

            else -> {
                Button(
                    onClick = {
                        if(style is VueButtonStyle.Stateful) {
                            if (style.currentState == VueButtonState.NORMAL)
                                onClick()
                        } else onClick()
                    },
                    colors = colors,
                    modifier = modifier,
                    enabled = if(style is VueButtonStyle.Stateful) {
                        if(style.currentState == VueButtonState.DISABLED) false
                        else enabled
                    } else enabled,
                    shape = shape,
                    elevation = elevation,
                    border = border,
                    contentPadding = contentPadding,
                    interactionSource = interactionSource,
                    content = content
                )
            }
        }

        if(style is VueButtonStyle.Stateful) {
            when(style.currentState) {
                VueButtonState.LOADING -> {
                    LoadingIndicatorOverlay(
                        modifier = Modifier.matchParentSize(),
                        shape = shape,
                        color = colors.contentColor
                    )
                }
                else -> { }
            }
        }
    }
}

@Composable
fun LoadingIndicatorOverlay(
    modifier: Modifier = Modifier,
    shape: Shape,
    color: Color
) {
    Box(
        modifier = modifier
            .background(
                color = VueColors.materialColorScheme().scrim.copy(0.4f),
                shape = shape
            )
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = color
        )
    }
}

@Composable
@Preview(showBackground = true)
fun VueSemanticButtonPreview(modifier: Modifier = Modifier) {
    VuePlayTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VueButton(
                style = VueButtonDefaults.Style.default(),
                onClick = {}
            ) { Text(text = "Link") }
        }
    }
}
