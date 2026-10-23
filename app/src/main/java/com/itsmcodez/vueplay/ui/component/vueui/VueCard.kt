package com.itsmcodez.vueplay.ui.component.vueui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.ui.theme.VuePlayTheme


@Immutable
sealed class VueCardType {
    data class Default(val containerColor: Color, val contentColor: Color) : VueCardType()
    data class Outlined(val containerColor: Color = Color.Transparent, val contentColor: Color) : VueCardType()
    data class Elevated(val containerColor: Color, val contentColor: Color, val elevation: Dp) : VueCardType()
}

object VueCardDefaults {

    class Type {
        companion object {
            @Composable
            fun default() = VueCardType.Default(
                containerColor = VueColors.materialColorScheme().surfaceContainer,
                contentColor = VueColors.materialColorScheme().contentColorFor(
                    VueColors.materialColorScheme().surfaceContainer
                )
            )
            @Composable
            fun outlined() = VueCardType.Outlined(
                containerColor = VueColors.materialColorScheme().surface,
                contentColor = VueColors.materialColorScheme().onSurface
            )
            @Composable
            fun elevated() = VueCardType.Elevated(
                containerColor = VueColors.materialColorScheme().surfaceContainer,
                contentColor = VueColors.materialColorScheme().contentColorFor(
                    VueColors.materialColorScheme().surfaceContainer
                ),
                elevation = VueElevation.Medium
            )
        }
    }

}

@Composable
fun VueCard(
    modifier: Modifier = Modifier,
    type: VueCardType = VueCardDefaults.Type.default(),
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    when(type) {
        is VueCardType.Default -> {
            Card(
                modifier = modifier,
                colors = CardDefaults.cardColors().copy(
                    containerColor = type.containerColor,
                    contentColor = type.contentColor
                ),
                onClick = onClick,
                content = content
            )
        }

        is VueCardType.Outlined -> {
            OutlinedCard(
                modifier = modifier,
                colors = CardDefaults.outlinedCardColors().copy(
                    containerColor = type.containerColor,
                    contentColor = type.contentColor
                ),
                onClick = onClick,
                content = content
            )
        }

        is VueCardType.Elevated -> {
            ElevatedCard(
                modifier = modifier,
                colors = CardDefaults.elevatedCardColors().copy(
                    containerColor = type.containerColor,
                    contentColor = type.contentColor
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = type.elevation),
                content = content
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun VueCardPreview() {
    VuePlayTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VueCard(
                type = VueCardDefaults.Type.outlined(),
                modifier = Modifier.size(360.dp),
            ) { Text(text = "hello") }
        }
    }
}
