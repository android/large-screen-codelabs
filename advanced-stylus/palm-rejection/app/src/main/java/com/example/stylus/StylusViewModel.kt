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
import android.view.MotionEvent
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.example.stylus.data.DrawPoint
import com.example.stylus.data.DrawPointType
import com.example.stylus.ui.StylusState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class StylusViewModel : ViewModel() {
    private var currentPath = mutableListOf<DrawPoint>()


    private var _stylusState = MutableStateFlow(StylusState())
    val stylusState: StateFlow<StylusState> = _stylusState


    private fun requestRendering(stylusState: StylusState) {
        // will update the stylusState which will trigger a Flow
        _stylusState.update {
            return@update stylusState
        }
    }

    private fun createPath(): Path {
        val path = Path()

        for (point in currentPath) {
            if (point.type == DrawPointType.START) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }
        return path
    }


    fun processMotionEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.add(
                    DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
                )
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
            }
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP -> {
                val canceled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        (motionEvent.flags and MotionEvent.FLAG_CANCELED) == MotionEvent.FLAG_CANCELED

                if(canceled) {
                    cancelLastStroke()
                } else {
                    currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                // unwanted touch detected
                cancelLastStroke()
            }
            else -> return false
        }


        requestRendering(
            StylusState(
                tilt = motionEvent.getAxisValue(MotionEvent.AXIS_TILT),
                pressure = motionEvent.pressure,
                orientation = motionEvent.orientation,
                path = createPath()
            )
        )
        return true
    }

    private fun cancelLastStroke() {
        // find the last "START" event
        val lastIndex = currentPath.findLastIndex {
            it.type == DrawPointType.START
        }

        // if found, keep element from 0 until very last event before last MOVE event
        if (lastIndex > 0) {
            currentPath = currentPath.subList(0, lastIndex - 1)
        }
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