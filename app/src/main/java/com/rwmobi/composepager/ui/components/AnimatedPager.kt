package com.rwmobi.composepager.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AnimatedViewPager(
    modifier: Modifier = Modifier,
    pageWidth: Dp,
    @DrawableRes drawables: List<Int>,
) {
    val pagerState = rememberPagerState(initialPage = 0)

    HorizontalPager(
        modifier = modifier,
        pageCount = drawables.size,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = pageWidth),
        verticalAlignment = Alignment.CenterVertically,
    ) { thisPageIndex ->
        PageLayout(
            modifier = Modifier.height(height = pageWidth),
            pagerState = pagerState,
            thisPageIndex = thisPageIndex,
            drawable = drawables[thisPageIndex],
        )
    }
}
