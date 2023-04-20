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

import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import com.example.stylus.data.Segment
import com.example.stylus.ui.StylusState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class StylusViewModel : ViewModel() {

    private var _stylusState = MutableStateFlow(StylusState())
    val stylusState: StateFlow<StylusState> = _stylusState

    val openGlLines = mutableListOf<List<Segment>>()

    private fun requestRendering(stylusState: StylusState) {
        // will update the stylusState which will trigger a Flow
        _stylusState.update {
            return@update stylusState
        }
    }


    private fun createStylusState(motionEvent: MotionEvent): StylusState {
        return StylusState(
            tilt = motionEvent.getAxisValue(MotionEvent.AXIS_TILT),
            pressure = motionEvent.pressure,
            orientation = motionEvent.orientation
        )
    }

    fun updateStylusVisualization(motionEvent: MotionEvent) {
        requestRendering(
            createStylusState(motionEvent)
        )
    }
}

inline fun <T> List<T>.findLastIndex(predicate: (T) -> Boolean): Int {
    val iterator = this.listIterator(size)
    var count = 1
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(element)) {
            return size - count
        }
        count++
    }
    return -1
}