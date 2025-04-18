/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.focusmanagementincompose

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.test.platform.app.InstrumentationRegistry
import com.example.focusmanagementincompose.ui.tab.FocusTraversalOrderTab
import org.junit.Rule
import org.junit.Test

class FocusTraversalOrderTabTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class)
    @Test
    fun testFocusTraversalOrder() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            LocalInputModeManager.current.requestInputMode(InputMode.Keyboard)
            FocusTraversalOrderTab(onClick = {})
        }

        // Ensure the 1st card is focused
        composeTestRule
            .onNodeWithText(context.getString(R.string.first_card))
            .requestFocus()
            .assertIsFocused()
            .performKeyInput {
                pressKey(Key.Tab)
            }

        // Test if focus moves to the 2nd card from the 1st card with Tab key
        composeTestRule
            .onNodeWithText(context.getString(R.string.second_card))
            .assertIsFocused()
            .performKeyInput {
                pressKey(Key.Tab)
            }

        // Test if focus moves to the 3nd card from the 2n card with Tab key
        composeTestRule
            .onNodeWithText(context.getString(R.string.third_card))
            .assertIsFocused()
            .performKeyInput {
                pressKey(Key.Tab)
            }

        // Test if focus moves to the 3nd card from the 2n card with Tab key
        composeTestRule
            .onNodeWithText(context.getString(R.string.fourth_card))
            .assertIsFocused()
            .performKeyInput {
                pressKey(Key.Tab)
            }
    }
}
