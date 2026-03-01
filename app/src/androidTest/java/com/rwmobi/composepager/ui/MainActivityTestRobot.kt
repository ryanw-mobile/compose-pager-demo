/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 * Sponsored by RW MobiMedia UK Limited
 *
 */

package com.rwmobi.composepager.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipeLeft
import com.rwmobi.composepager.R
import com.rwmobi.composepager.ui.test.ComposePagerTestRule

internal class MainActivityTestRobot(
    private val composeTestRule: ComposePagerTestRule,
) {
    // Actions
    fun printSemanticTree() {
        with(composeTestRule) {
            onRoot().printToLog("SemanticTree")
        }
    }

    fun swipeLeft() {
        with(composeTestRule) {
            onNodeWithContentDescription(label = activity.getString(R.string.content_description_viewpager))
                .performTouchInput {
                    // Swipe by about 1/3 of the screen width to move one page
                    swipeLeft(startX = center.x + 100f, endX = center.x - 100f)
                }
            waitForIdle()
        }
    }

    fun performClickOnPage(index: Int, total: Int) {
        with(composeTestRule) {
            onNodeWithContentDescription(label = activity.getString(R.string.content_description_page_item, index, total))
                .performClick()
            waitForIdle()
        }
    }

    // Assertions
    fun assertViewPagerIsDisplayed() {
        with(composeTestRule) {
            onNodeWithContentDescription(label = activity.getString(R.string.content_description_viewpager)).assertIsDisplayed()
        }
    }

    fun assertPageIsDisplayed(index: Int, total: Int) {
        with(composeTestRule) {
            onNodeWithContentDescription(
                label = activity.getString(R.string.content_description_page_item, index, total),
                substring = true,
            ).assertIsDisplayed()
        }
    }

    fun assertPageIsActive(index: Int, total: Int) {
        with(composeTestRule) {
            val baseDescription = activity.getString(R.string.content_description_page_item, index, total)
            val activeDescription = activity.getString(R.string.content_description_active_item, baseDescription)
            onNodeWithContentDescription(label = activeDescription).assertIsDisplayed()
        }
    }
}
