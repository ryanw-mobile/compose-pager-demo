package com.rwmobi.composepager.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import com.rwmobi.composepager.ui.pagerAnimation

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AnimatedViewPager(
    modifier: Modifier = Modifier,
    pageSize: Dp,
    @DrawableRes drawables: List<Int>,
) {
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { drawables.size },
        )

    var currentPageIndex by remember { mutableIntStateOf(0) }
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { currentPage ->
            if (currentPageIndex != currentPage) {
                hapticFeedback.performHapticFeedback(
                    hapticFeedbackType = HapticFeedbackType.LongPress,
                )
                currentPageIndex = currentPage
            }
            // Anything to be triggered by page-change can be done here
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = pageSize),
        verticalAlignment = Alignment.CenterVertically,
    ) { thisPageIndex ->
        PageLayout(
            modifier =
                Modifier
                    .size(size = pageSize)
                    .pagerAnimation(
                        pagerState = pagerState,
                        thisPageIndex = thisPageIndex,
                    ),
            drawable = drawables[thisPageIndex],
        )
    }
}
