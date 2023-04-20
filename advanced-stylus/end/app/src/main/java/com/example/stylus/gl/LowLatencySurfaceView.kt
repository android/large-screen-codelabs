/*
* Copyright (c) 2022 Google LLC
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
package com.example.stylus.gl

import android.content.Context
import android.os.Build
import android.view.SurfaceView
import androidx.annotation.RequiresApi

class LowLatencySurfaceView(context: Context, private val fastRenderer: FastRenderer) :
    SurfaceView(context) {

    init {
        setOnTouchListener(fastRenderer.onTouchListener)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fastRenderer.attachSurfaceView(this)
    }

    override fun onDetachedFromWindow() {
        fastRenderer.release()
        super.onDetachedFromWindow()
    }
}