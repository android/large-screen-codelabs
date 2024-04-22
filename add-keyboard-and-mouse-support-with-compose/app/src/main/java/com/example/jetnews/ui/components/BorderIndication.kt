/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetnews.ui.components

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class BorderIndication(
    val brush: Brush = BorderIndicationDefault.brush,
    val offset: DpOffset = BorderIndicationDefault.offset,
    val borderWidth: Dp = BorderIndicationDefault.borderWidth,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return OutlineIndicationNode(
            interactionSource,
            brush,
            offset,
            borderWidth,
        )
    }
}

data object BorderIndicationDefault {
    val brush: Brush = SolidColor(Color.LightGray)
    val offset: DpOffset = DpOffset(8.dp, 0.dp)
    val borderWidth: Dp = 2.dp
}

class OutlineIndicationNode(
    private val interactionSource: InteractionSource,
    private val brush: Brush,
    private val offset: DpOffset,
    private val borderWidth: Dp,
) : Modifier.Node(), DrawModifierNode {

    private var isFocused by mutableStateOf(false)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.map {
                it is FocusInteraction.Focus
            }.collect {
                isFocused = it
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (isFocused) {
            translate(offset.x.toPx(), offset.y.toPx()) {
                drawLine(
                    brush,
                    Offset(size.width, 0f),
                    Offset(size.width, size.height),
                    borderWidth.toPx()
                )
            }
        }
    }
}
