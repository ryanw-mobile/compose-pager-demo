package com.rwmobi.composepager.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rwmobi.composepager.R
import com.rwmobi.composepager.ui.components.AnimatedViewPager
import com.rwmobi.composepager.ui.theme.ComposePagerTheme

@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
internal fun MainScreen(modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val pageWidth = (screenWidth / 3f).dp
    val drawables = listOf(
        R.drawable.greggs1,
        R.drawable.greggs2,
        R.drawable.greggs3,
        R.drawable.greggs4,
        R.drawable.greggs5,
        R.drawable.greggs6,
        R.drawable.greggs7,
    )

    ComposePagerTheme {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                AnimatedViewPager(
                    modifier = Modifier.fillMaxWidth(),
                    pageSize = pageWidth, // Page is in square shape
                    drawables = drawables,
                )
            }
        }
    }
}
