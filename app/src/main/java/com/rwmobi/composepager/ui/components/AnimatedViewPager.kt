package com.rwmobi.composepager.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import com.rwmobi.composepager.R
import com.rwmobi.composepager.ui.pagerAnimation
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AnimatedViewPager(
    modifier: Modifier = Modifier,
    pageSize: Dp,
    @DrawableRes drawables: List<Int>,
) {
    if (drawables.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "No content available")
        }
        return
    }

    val endlessPagerMultiplier = 1000
    val pageCount = endlessPagerMultiplier * drawables.size
    val initialPage = pageCount / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f,
        pageCount = { pageCount },
    )

    val scope = rememberCoroutineScope()
    var currentPageIndex by remember { mutableIntStateOf(initialPage) }
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

    val context = LocalContext.current
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = pageSize),
        verticalAlignment = Alignment.CenterVertically,
    ) { absolutePageIndex ->
        val resolvedPageContentIndex = absolutePageIndex % drawables.size
        val isCurrentPage = pagerState.currentPage == absolutePageIndex

        val baseDescription = context.getString(
            R.string.content_description_page_item,
            resolvedPageContentIndex + 1,
            drawables.size,
        )
        val finalDescription = if (isCurrentPage) {
            context.getString(R.string.content_description_active_item, baseDescription)
        } else {
            baseDescription
        }

        PageLayout(
            modifier = Modifier
                .size(size = pageSize)
                .pagerAnimation(
                    pagerState = pagerState,
                    thisPageIndex = absolutePageIndex,
                )
                .clickable {
                    scope.launch {
                        pagerState.animateScrollToPage(absolutePageIndex)
                    }
                },
            drawable = drawables[resolvedPageContentIndex],
            contentDescription = finalDescription,
        )
    }
}
