package com.ladamengh.petproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatlasNavigationUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun userCanOpenProfileFromHomeTab() {
        // Given the app is open on the Home tab, When the user opens Profile...
        composeRule.onNodeWithContentDescription("Профиль").performClick()

        // Then the profile screen and its primary action are visible.
        composeRule.onNodeWithText("Профиль").assertIsDisplayed()
        composeRule.onNodeWithText("Редактировать").assertIsDisplayed()
    }
}
