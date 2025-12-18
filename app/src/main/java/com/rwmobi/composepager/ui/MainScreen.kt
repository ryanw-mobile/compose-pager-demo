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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    val resources = LocalResources.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val pageWidth = (screenWidth / 3f).dp
    val drawables =
        listOf(
            R.drawable.meal1,
            R.drawable.meal2,
            R.drawable.meal3,
            R.drawable.meal4,
            R.drawable.meal5,
            R.drawable.meal6,
            R.drawable.meal7,
            R.drawable.meal8,
            R.drawable.meal9,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = resources.getString(R.string.content_description_viewpager) },
                    pageSize = pageWidth, // Page is in square shape
                    drawables = drawables,
                )
            }
        }
    }
}
