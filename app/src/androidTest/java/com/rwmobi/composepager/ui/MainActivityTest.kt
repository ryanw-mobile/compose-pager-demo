/*
 * Copyright (c) 2024. Ryan Wong
 * https://github.com/ryanw-mobile
 * Sponsored by RW MobiMedia UK Limited
 *
 */

package com.rwmobi.composepager.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rwmobi.composepager.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@kotlinx.coroutines.ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mainActivityTestRobot: MainActivityTestRobot

    @Before
    fun setUp() {
        mainActivityTestRobot = MainActivityTestRobot(composeTestRule)
    }

    @Test
    fun appLaunchSuccessfully() {
        with(mainActivityTestRobot) {
            assertViewPagerIsDisplayed()
            assertPageIsActive(index = 1, total = 9)
        }
    }

    @Test
    fun swipeLeftChangesPage() {
        with(mainActivityTestRobot) {
            assertViewPagerIsDisplayed()
            assertPageIsActive(index = 1, total = 9)
            swipeLeft()
            assertPageIsActive(index = 2, total = 9)
        }
    }

    @Test
    fun clickPageChangesPage() {
        with(mainActivityTestRobot) {
            assertViewPagerIsDisplayed()
            assertPageIsActive(index = 1, total = 9)
            performClickOnPage(index = 2, total = 9)
            assertPageIsActive(index = 2, total = 9)
        }
    }
}
