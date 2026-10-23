package com.itsmcodez.vueplay.ui.component.vueui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.ui.theme.VuePlayTheme

@Immutable
data class VueNavigationBarItem(
    val selected: Boolean,
    val label: String,
    @DrawableRes val unselectedIcon: Int,
    @DrawableRes val selectedIcon: Int = unselectedIcon
)

@Composable
fun VueNavigationBar(
    modifier: Modifier = Modifier,
    items: List<VueNavigationBarItem>,
    showSelectedLabel: Boolean = true,
    showUnselectedLabel: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.onBackground,
    unselectedColor: Color = Color.Gray.copy(alpha = 0.7f),
    onNavigationBarItemSelected: (VueNavigationBarItem) -> Unit
) {
    var selectedIndex by rememberSaveable(items) { mutableIntStateOf(
        if(items.indexOfFirst { it.selected } != -1) items.indexOfFirst { it.selected }
            else 0
    ) }
    var navItems by remember(selectedIndex, items) {
        mutableStateOf(
            items.mapIndexed { index, item ->
                item.copy(selected = index == selectedIndex)
            }
        )
    }

    Column(
        modifier = modifier
            .windowInsetsPadding(NavigationBarDefaults.windowInsets)
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider()

        Box(
            modifier = Modifier.widthIn(
                min = 320.dp,
                max = if (showUnselectedLabel) 600.dp else minOf(600.dp, 320.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            NavigationBar(
                modifier = Modifier.height(if (showSelectedLabel || showUnselectedLabel) 64.dp else 56.dp),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.background),
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIndicatorColor = Color.Transparent,
                            selectedTextColor = selectedColor,
                            selectedIconColor = selectedColor,
                            unselectedIconColor = unselectedColor,
                            unselectedTextColor = unselectedColor
                        ),
                        alwaysShowLabel = showUnselectedLabel,
                        selected = item.selected,
                        onClick = { selectedIndex = index; onNavigationBarItemSelected(item) },
                        icon = {
                            Icon(
                                modifier = Modifier.then(
                                    if(!showUnselectedLabel) Modifier.size(32.dp)
                                    else Modifier
                                ),
                                painter = painterResource(if (item.selected) item.selectedIcon else item.unselectedIcon),
                                contentDescription = item.label
                            )
                        },
                        label = if (showSelectedLabel || showUnselectedLabel) {
                            { Text(text = item.label) }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun VueNavigationBarPreview() {
    VuePlayTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                VueNavigationBar(
                    showUnselectedLabel = false,
                    showSelectedLabel = true,
                    items = listOf(
                        VueNavigationBarItem(false, "Home", R.drawable.rounded_home_24),
                        VueNavigationBarItem(false, "Post", R.drawable.rounded_add_24),
                        VueNavigationBarItem(false, "Search", R.drawable.rounded_search_24),
                        //VueNavigationBarItem(false, "About", R.drawable.rounded_folder_24),
                        //VueNavigationBarItem(false, "Help", R.drawable.rounded_pip_exit_24)
                    )
                ) {}
            }
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(it),
                Alignment.Center
            ) { Text(
                text = "Content",
                style = MaterialTheme.typography.headlineLarge
            ) }
        }
    }
}
