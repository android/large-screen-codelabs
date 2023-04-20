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
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import androidx.annotation.WorkerThread
import androidx.graphics.lowlatency.BufferInfo
import androidx.core.graphics.toColor
import androidx.graphics.lowlatency.GLFrontBufferedRenderer
import androidx.graphics.opengl.egl.EGLManager
import androidx.input.motionprediction.MotionEventPredictor
import com.example.stylus.StylusViewModel
import com.example.stylus.data.Segment


class FastRenderer(
    private var viewModel: StylusViewModel
) : GLFrontBufferedRenderer.Callback<Segment> {
    private val mvpMatrix = FloatArray(16)
    private val projection = FloatArray(16)

    private var frontBufferRenderer: GLFrontBufferedRenderer<Segment>? = null
    private var motionEventPredictor: MotionEventPredictor? = null

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

        obtainRenderer().drawLine(projection, listOf(param), Color.GRAY.toColor())
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

        // Clear the screen with black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        viewModel.openGlLines.add(params.toList())

        // Render the entire scene (all lines)
        for (line in viewModel.openGlLines) {
            obtainRenderer().drawLine(projection, line, Color.GRAY.toColor())
        }
    }

    fun attachSurfaceView(surfaceView: SurfaceView) {
        frontBufferRenderer = GLFrontBufferedRenderer(surfaceView, this)
        motionEventPredictor = MotionEventPredictor.newInstance(surfaceView)
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
        motionEventPredictor?.record(event)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Ask that the input system not batch MotionEvents
                // but instead deliver them as soon as they're available
                view.requestUnbufferedDispatch(event)

                currentX = event.x
                currentY = event.y

                // Create single point
                val segment = Segment(currentX, currentY, currentX, currentY)

                frontBufferRenderer?.renderFrontBufferedLayer(segment)

            }
            MotionEvent.ACTION_MOVE -> {
                previousX = currentX
                previousY = currentY
                currentX = event.x
                currentY = event.y

                val segment = Segment(previousX, previousY, currentX, currentY)

                // Send the short line to front buffered layer: fast rendering
                frontBufferRenderer?.renderFrontBufferedLayer(segment)

                val motionEventPredicted = motionEventPredictor?.predict()
                if(motionEventPredicted != null) {
                    val predictedSegment = Segment(currentX, currentY,
                        motionEventPredicted.x, motionEventPredicted.y)
                    frontBufferRenderer?.renderFrontBufferedLayer(predictedSegment)
                }
            }
            MotionEvent.ACTION_UP -> {
                frontBufferRenderer?.commit()
            }
        }
        true
    }
}