/*
* Copyright (c) 2023 Google LLC
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.stylus

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.stylus.ui.theme.StylusTheme
import androidx.compose.ui.draw.clipToBounds

class MainActivity : ComponentActivity() {

    private val viewModel: StylusViewModel by viewModels()
    private val strokeStyle = Stroke(10F)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StylusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        StylusVisualization(modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp))
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.Black
                        )
                        DrawArea(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }

    @Composable
    fun StylusVisualization(modifier: Modifier = Modifier) {
        Canvas(
            modifier = modifier
        ) {

        }
    }

    @Composable

    fun DrawArea(modifier: Modifier = Modifier) {
        Canvas(modifier = modifier
            .clipToBounds()

        ) {

        }
    }
}