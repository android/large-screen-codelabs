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

import StylusVisualization.drawOrientation
import StylusVisualization.drawPressure
import StylusVisualization.drawTilt
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stylus.ui.theme.StylusTheme
import com.example.stylus.ui.StylusState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.stylus.gl.FastRenderer
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: StylusViewModel by viewModels()

    private var stylusState: StylusState by mutableStateOf(StylusState())

    private lateinit var fastRendering: FastRenderer

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stylusState
                    .onEach {
                        stylusState = it
                    }
                    .collect()
            }
        }

        fastRendering = FastRenderer(viewModel)

        setContent {
            StylusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        StylusVisualization(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                        Divider(
                            thickness = 1.dp,
                            color = Color.Black,
                        )
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
            with(stylusState) {
                drawOrientation(this.orientation)
                drawTilt(this.tilt)
                drawPressure(this.pressure)
            }
        }
    }
}