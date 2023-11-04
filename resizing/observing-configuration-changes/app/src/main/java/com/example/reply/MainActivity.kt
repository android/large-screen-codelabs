/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.reply

import android.content.res.Configuration
import android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
import android.content.res.Configuration.SCREENLAYOUT_SIZE_NORMAL
import android.content.res.Configuration.SCREENLAYOUT_SIZE_SMALL
import android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import com.example.reply.ui.ReplyApp
import com.example.reply.ui.ReplyViewModel
import com.example.reply.ui.theme.ReplyTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This instance of the ReplyViewModel and call to initializeUIState() will be removed later in the codelab
        val viewModel: ReplyViewModel by viewModels()
        viewModel.initializeUIState()

        setContent {
            ReplyTheme {
                Surface {
                    //Temporarily replace the app's UI with this composable to see config changes
                    DisplayConfigValues()
                }
            }
        }
    }
}

/**
 * Used temporarily for demonstration purposes only to visualize configuration changes in real time
 */
@Composable
fun DisplayConfigValues() {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val screenLayoutSize =
        when (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            SCREENLAYOUT_SIZE_SMALL -> "SCREENLAYOUT_SIZE_SMALL"
            SCREENLAYOUT_SIZE_NORMAL -> "SCREENLAYOUT_SIZE_NORMAL"
            SCREENLAYOUT_SIZE_LARGE -> "SCREENLAYOUT_SIZE_LARGE"
            SCREENLAYOUT_SIZE_XLARGE -> "SCREENLAYOUT_SIZE_XLARGE"
            else -> "undefined value"
        }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("screenWidthDp: ${configuration.screenWidthDp}")
        Text("screenHeightDp: ${configuration.screenHeightDp}")
        Text("smallestScreenWidthDp: ${configuration.smallestScreenWidthDp}")
        Text("orientation: ${if (isPortrait) "portrait" else "landscape"}")
        Text("screenLayout SIZE: $screenLayoutSize")
    }
}

@Preview(showBackground = true)
@Composable
fun ReplyAppCompactPreview() {
    ReplyTheme {
        Surface {
            ReplyApp(
                windowSize = WindowWidthSizeClass.Compact,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun ReplyAppMediumPreview() {
    ReplyTheme {
        Surface {
            ReplyApp(
                windowSize = WindowWidthSizeClass.Medium,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
fun ReplyAppExpandedPreview() {
    ReplyTheme {
        Surface {
            ReplyApp(
                windowSize = WindowWidthSizeClass.Expanded,
            )
        }
    }
}
