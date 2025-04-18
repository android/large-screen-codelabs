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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.focusmanagementincompose.R
import com.example.focusmanagementincompose.ui.component.FirstCard
import com.example.focusmanagementincompose.ui.component.ThirdCard

@Composable
fun FocusTargetTab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        FirstCard(
            onClick = onClick,
            modifier = Modifier.width(240.dp)
        )
        SecondCard(
            modifier = Modifier.width(240.dp)
        )
        ThirdCard(
            onClick = onClick,
            modifier = Modifier.width(240.dp)
        )
    }
}

@Composable
private fun SecondCard(
    modifier: Modifier = Modifier,
) = Surface(
    shape = CardDefaults.shape,
    color = CardDefaults.cardColors().containerColor,
    modifier = modifier
        .aspectRatio(ratio = 1.777f) // 16:9
        .background(MaterialTheme.colorScheme.surface),
) {
    Text(text = stringResource(R.string.second_card), modifier = Modifier.padding(32.dp))
}
