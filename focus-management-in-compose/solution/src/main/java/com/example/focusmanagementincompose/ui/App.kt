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

package com.example.focusmanagementincompose.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.focusmanagementincompose.R
import com.example.focusmanagementincompose.ui.tab.FocusGroupTab
import com.example.focusmanagementincompose.ui.tab.FocusTargetTab
import com.example.focusmanagementincompose.ui.tab.FocusTraversalOrderTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val backstack = rememberNavBackStack(Tab.FocusTarget)
    val selectedTabIndex = Tab.entries.indexOf(backstack.last())
    val focusRequesters = remember {
        List(Tab.entries.size) { FocusRequester() }
    }

    Column(modifier = modifier) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .focusProperties {
                    onEnter = {
                        focusRequesters[selectedTabIndex].requestFocus()
                    }
                }
                .focusGroup()
        ) {
            Tab.entries.forEachIndexed { index, tab ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            backstack.add(tab)
                        }
                    },
                    text = { Text(stringResource(tab.title)) },
                    modifier = Modifier.focusRequester(focusRequester = focusRequesters[index])
                )
            }
        }
        NavDisplay(
            backStack = backstack,
            entryProvider = entryProvider {
                entry<Tab.FocusTarget> {
                    FocusTargetTab(
                        onClick = context::onCardClicked,
                        modifier = Modifier.padding(32.dp),
                    )
                }
                entry<Tab.FocusTraversalOrder> {
                    FocusTraversalOrderTab(
                        onClick = context::onCardClicked,
                        modifier = Modifier.padding(32.dp)
                    )
                }
                entry<Tab.FocusGroup> {
                    FocusGroupTab(
                        onClick = context::onCardClicked,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        )
    }
}

private fun Context.onCardClicked() {
    Toast.makeText(
        this,
        getString(R.string.toast_message),
        Toast.LENGTH_SHORT
    ).show()
}
