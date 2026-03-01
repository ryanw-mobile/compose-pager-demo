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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.rwmobi.composepager.R
import com.rwmobi.composepager.ui.pagerAnimation
import kotlinx.coroutines.Job
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
            Text(text = stringResource(id = R.string.empty_state_message))
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
    var scrollJob by remember { mutableStateOf<Job?>(null) }
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

    val resources = LocalResources.current
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = pageSize),
        verticalAlignment = Alignment.CenterVertically,
    ) { absolutePageIndex ->
        // Defensive calculation to ensure indices stay valid during rapid interactions
        val resolvedPageContentIndex = remember(absolutePageIndex, drawables) {
            absolutePageIndex % drawables.size
        }

        val isCurrentPage by remember(absolutePageIndex) {
            derivedStateOf { pagerState.currentPage == absolutePageIndex }
        }

        val finalDescription = remember(isCurrentPage, resolvedPageContentIndex) {
            val baseDescription = resources.getString(
                R.string.content_description_page_item,
                resolvedPageContentIndex + 1,
                drawables.size,
            )
            if (isCurrentPage) {
                resources.getString(R.string.content_description_active_item, baseDescription)
            } else {
                baseDescription
            }
        }

        PageLayout(
            modifier = Modifier
                .size(size = pageSize)
                .pagerAnimation(
                    pagerState = pagerState,
                    thisPageIndex = absolutePageIndex,
                )
                .clickable {
                    // Prevent concurrent scroll animations to avoid race conditions and potential crashes
                    scrollJob?.cancel()
                    scrollJob = scope.launch {
                        // Safety check: target must be within current bounds
                        if (absolutePageIndex < pagerState.pageCount) {
                            pagerState.animateScrollToPage(absolutePageIndex)
                        }
                    }
                },
            drawable = drawables[resolvedPageContentIndex],
            contentDescription = finalDescription,
        )
    }
}
