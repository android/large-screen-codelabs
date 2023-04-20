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

package com.example.stylus.gl

import android.annotation.SuppressLint
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import androidx.annotation.WorkerThread
import androidx.graphics.lowlatency.BufferInfo
import androidx.graphics.lowlatency.GLFrontBufferedRenderer
import androidx.graphics.opengl.egl.EGLManager
import com.example.stylus.StylusViewModel
import com.example.stylus.data.Segment


class FastRenderer(
    private var viewModel: StylusViewModel
) : GLFrontBufferedRenderer.Callback<Segment> {
    private val mvpMatrix = FloatArray(16)
    private val projection = FloatArray(16)

    private var frontBufferRenderer: GLFrontBufferedRenderer<Segment>? = null

    private var lineRenderer: LineRenderer = LineRenderer()

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private var currentX: Float = 0f
    private var currentY: Float = 0f

    @WorkerThread // GLThread
    private fun obtainRenderer(): LineRenderer =
        if (lineRenderer.isInitialized) {
            lineRenderer
        } else {
            lineRenderer
                .apply {
                    initialize()
                }
        }

    override fun onDrawFrontBufferedLayer(
        eglManager: EGLManager,
        bufferInfo: BufferInfo,
        transform: FloatArray,
        param: Segment
    ) {
        val bufferWidth = bufferInfo.width
        val bufferHeight = bufferInfo.height
        GLES20.glViewport(0, 0, bufferWidth, bufferHeight)
        // Map Android coordinates to GL coordinates
        Matrix.orthoM(
            mvpMatrix,
            0,
            0f,
            bufferWidth.toFloat(),
            0f,
            bufferHeight.toFloat(),
            -1f,
            1f
        )

        Matrix.multiplyMM(projection, 0, mvpMatrix, 0, transform, 0)
    }

    override fun onDrawDoubleBufferedLayer(
        eglManager: EGLManager,
        bufferInfo: BufferInfo,
        transform: FloatArray,
        params: Collection<Segment>
    ) {
        val bufferWidth = bufferInfo.width
        val bufferHeight = bufferInfo.height
        // define the size of the rectangle for rendering
        GLES20.glViewport(0, 0, bufferWidth, bufferHeight)
        // Computes the ModelViewProjection Matrix
        Matrix.orthoM(
            mvpMatrix,
            0,
            0f,
            bufferWidth.toFloat(),
            0f,
            bufferHeight.toFloat(),
            -1f,
            1f
        )
        // perform matrix multiplication to transform the Android data to OpenGL reference
        Matrix.multiplyMM(projection, 0, mvpMatrix, 0, transform, 0)
    }

    fun attachSurfaceView(surfaceView: SurfaceView) {
        frontBufferRenderer = GLFrontBufferedRenderer(surfaceView, this)
    }

    fun release() {
        frontBufferRenderer?.release(true)
        {
            obtainRenderer().release()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    val onTouchListener = View.OnTouchListener { view, event ->

        viewModel.updateStylusVisualization(event)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {

            }
        }
        true
    }
}