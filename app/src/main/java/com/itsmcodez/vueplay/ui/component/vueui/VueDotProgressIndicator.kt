package com.itsmcodez.vueplay.ui.component.vueui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.itsmcodez.vueplay.ui.theme.VuePlayTheme
import kotlinx.coroutines.delay
import kotlin.math.floor
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun WormPagerSample() {
    val pageCount = 4
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()

    VuePlayTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(
                            color = when(page) {
                                0 -> Color.Red
                                1 -> Color.Green
                                2 -> Color.Blue
                                else -> Color.Yellow
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Page Content
                }
            }

            VueDotProgressIndicator(
                pagerState = pagerState,
                pageCount = pageCount,
                modifier = Modifier.padding(vertical = 32.dp),
                onPageSelected = { page ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )
        }
    }
}

/**
 * Use this for limited pages only, or else use [VueDotProgressIndicator] with [CarouselState] param
 * for dynamic pages
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VueDotProgressIndicator(
    pagerState: PagerState,
    pageCount: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = VueColors.materialColorScheme().onSurface,
    unselectedColor: Color = Color.Gray.copy(alpha = 0.5f)
) {
    val dotSize = 8.dp
    val spacing = 8.dp

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { index ->
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(unselectedColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onPageSelected(index)
                        }
                )
            }
        }

        // 2. Worm Indicator Overlay
        // Calculate current scrolling progress
        val currentPosition = pagerState.currentPage
        val offsetFraction = pagerState.currentPageOffsetFraction

        // Math to determine stretch width and horizontal shift
        val totalDistance = dotSize + spacing
        val positionOffset = currentPosition * totalDistance

        val startOffset = if (offsetFraction > 0) {
            positionOffset + (offsetFraction * totalDistance)
        } else {
            positionOffset
        }

        val endOffset = if (offsetFraction < 0) {
            positionOffset + (offsetFraction * totalDistance)
        } else {
            positionOffset
        }

        // Calculate dynamic width stretching based on swiping fraction
        val stretchFactor = abs(offsetFraction) * totalDistance
        val dynamicWidth = dotSize + stretchFactor

        // Shift left edge back if swiping backwards to anchor the stretch
        val finalLeftPadding = if (offsetFraction < 0) {
            startOffset + (offsetFraction * totalDistance)
        } else {
            positionOffset
        }

        Box(
            modifier = Modifier
                .padding(start = finalLeftPadding)
                .size(width = dynamicWidth, height = dotSize)
                .clip(CircleShape)
                .background(selectedColor)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun FullDynamicCarouselSample() {
    val items = remember { (1..15).map { "Slide $it" } }
    val carouselState = rememberCarouselState { items.size }
    val coroutineScope = rememberCoroutineScope()

    VuePlayTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalMultiBrowseCarousel(
                state = carouselState,
                preferredItemWidth = 400.dp,
                itemSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .maskClip(MaterialTheme.shapes.extraLarge), // Proper M3 masking
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = items[index], fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            VueDotProgressIndicator(
                carouselState = carouselState,
                itemCount = items.size,
                maxVisibleDots = 5,
                autoScrollInterval = 5000L,
                hapticsEnabled = true,
                modifier = Modifier.padding(bottom = 16.dp),
                onPageSelected = { index ->
                    coroutineScope.launch {
                        carouselState.animateScrollToItem(index)
                    }
                }
            )
        }
    }
}

/**
 * Use this for dynamic pages only, or else use [VueDotProgressIndicator] with [PagerState] param
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VueDotProgressIndicator(
    carouselState: CarouselState,
    itemCount: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisibleDots: Int = 5,
    autoScrollInterval: Long = 0L,   // Default: 0 = disabled
    hapticsEnabled: Boolean = false, // Default: disabled
    dotSize: Dp = 8.dp,
    spacing: Dp = 6.dp,
    selectedColor: Color = VueColors.materialColorScheme().onSurface,
    unselectedColor: Color = Color.Gray.copy(alpha = 0.5f)
) {
    if (itemCount <= 1) return

    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    // Deconstruct positions using derivedStateOf to prevent premature early-returns
    val currentFloatPosition by remember(carouselState, itemCount) {
        derivedStateOf {
            val indicatorState = carouselState.scrollIndicatorState
            val rawOffset = indicatorState?.scrollOffset ?: 0
            val totalContentSize = indicatorState?.contentSize ?: 0

            if (totalContentSize > 0f && itemCount > 0) {
                val estimatedItemWidthPixels = (totalContentSize / itemCount).toFloat()
                rawOffset / estimatedItemWidthPixels
            } else {
                // Secure structural fallback to prevent hiding the indicator layout entirely
                carouselState.currentItem.toFloat()
            }
        }
    }

    val currentPosition = remember(currentFloatPosition) {
        floor(currentFloatPosition).toInt().coerceIn(0, itemCount - 1)
    }
    val offsetFraction = currentFloatPosition - currentPosition
    var lazyRowLeftPx by remember { mutableFloatStateOf(0f) }

    // 1. Core Feature: OPTIONAL AUTO-SCROLL LOOP
    if (autoScrollInterval > 0L) {
        LaunchedEffect(autoScrollInterval, itemCount) {
            while (true) {
                delay(autoScrollInterval.milliseconds)
                // If user is actively pulling, wait out a cycle instead of interfering
                if (!carouselState.isScrollInProgress) {
                    val nextTarget = (carouselState.currentItem + 1) % itemCount
                    carouselState.animateScrollToItem(nextTarget)
                }
            }
        }
    }

    // 2. Window Sliding Management for Dynamic Lists
    LaunchedEffect(currentPosition) {
        val centerIndex = maxVisibleDots / 2
        val targetScrollPosition = (currentPosition - centerIndex).coerceAtLeast(0)
        listState.animateScrollToItem(targetScrollPosition)
    }

    val visibleWidth = (dotSize * maxVisibleDots) + (spacing * (maxVisibleDots - 1))

    Box(
        modifier = modifier.width(visibleWidth),
        contentAlignment = Alignment.CenterStart
    ) {
        // LAYER 1: Memory-efficient track window holding unselected background units
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    lazyRowLeftPx = coordinates.localToWindow(BoxRootCoordinates.Zero).x
                }
        ) {
            items(itemCount) { index ->
                val distance = abs(index - currentFloatPosition)

                // Edge calculations to dynamically shrink boundary dots
                val scale = when {
                    distance <= 1.0f -> 1.0f
                    distance <= 2.0f -> 0.7f
                    distance <= 3.0f -> 0.4f
                    else -> 0.0f
                }

                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(CircleShape)
                        .background(unselectedColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            // 3. Core Feature: OPTIONAL HAPTIC SNAP EFFECT
                            if (hapticsEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            onPageSelected(index)
                        }
                )
            }
        }

        // LAYER 2: Relative Floating Worm Tracker Layer
        val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
        val activeItemInfo = visibleItemsInfo.find { it.index == currentPosition }
        val targetItemInfo = visibleItemsInfo.find { it.index == currentPosition + 1 }

        // Secure fallback to native static coordinate mapping calculations if lazy array tree info isn't measured yet
        val activeDotLeftPx = activeItemInfo?.let {
            (listState.layoutInfo.viewportStartOffset + it.offset).toFloat()
        } ?: with(density) { (currentPosition * (dotSize + spacing).toPx()) }

        val targetDotLeftPx = targetItemInfo?.let {
            (listState.layoutInfo.viewportStartOffset + it.offset).toFloat()
        } ?: (activeDotLeftPx + with(density) { (dotSize + spacing).toPx() })

        val totalDistancePx = targetDotLeftPx - activeDotLeftPx
        val stretchFactorPx = abs(offsetFraction) * totalDistancePx
        val dynamicWidthDp = with(density) { (dotSize.toPx() + stretchFactorPx).toDp() }

        val finalLeftPaddingDp = with(density) {
            val calculatedPadding = if (offsetFraction < 0) {
                activeDotLeftPx + (offsetFraction * totalDistancePx)
            } else {
                activeDotLeftPx
            }
            calculatedPadding.toDp()
        }

        Box(
            modifier = Modifier
                .padding(start = finalLeftPaddingDp)
                .size(width = dynamicWidthDp, height = dotSize)
                .clip(CircleShape)
                .background(selectedColor)
        )
    }
}

private object BoxRootCoordinates {
    val Zero = androidx.compose.ui.geometry.Offset.Zero
}


