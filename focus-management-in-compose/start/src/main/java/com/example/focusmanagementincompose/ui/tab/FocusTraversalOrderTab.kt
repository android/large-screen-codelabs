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

package com.example.focusmanagementincompose.ui.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.focusmanagementincompose.ui.component.FirstCard
import com.example.focusmanagementincompose.ui.component.FourthCard
import com.example.focusmanagementincompose.ui.component.SecondCard
import com.example.focusmanagementincompose.ui.component.ThirdCard

@Composable
fun FocusTraversalOrderTab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FirstCard(
                onClick = onClick,
                modifier = Modifier.width(240.dp)
            )
            FourthCard(
                onClick = onClick,
                modifier = Modifier
                    .width(240.dp)
                    .offset(x = 256.dp)
            )
            ThirdCard(
                onClick = onClick,
                modifier = Modifier
                    .width(240.dp)
                    .offset(y = (-151).dp)
            )
        }
        SecondCard(
            onClick = onClick,
            modifier = Modifier.width(240.dp)
        )
    }
}
