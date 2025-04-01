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

package com.example.focusmanagementincompose.ui.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.focusmanagementincompose.R

@Composable
fun TextCard(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.aspectRatio(
            ratio = 1.777f,
        ), // 16:9
        onClick = onClick
    ) {
        Text(text = title, modifier = Modifier.padding(32.dp))
    }
}

@Composable
fun FirstCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) = TextCard(
    title = stringResource(R.string.first_card),
    modifier = modifier,
    onClick = onClick
)

@Composable
fun SecondCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) = TextCard(
    title = stringResource(R.string.second_card),
    modifier = modifier,
    onClick = onClick
)

@Composable
fun ThirdCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) = TextCard(
    title = stringResource(R.string.third_card),
    modifier = modifier,
    onClick = onClick
)

@Composable
fun FourthCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) = TextCard(
    title = stringResource(R.string.fourth_card),
    modifier = modifier,
    onClick = onClick
)
