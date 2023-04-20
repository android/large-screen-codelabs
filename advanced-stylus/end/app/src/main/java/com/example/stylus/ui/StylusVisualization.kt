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
import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object StylusVisualization {
    private const val radius = 100f
    private const val minorRadius = 10f
    private const val margin = 20f
    private const val minorMargin = minorRadius / 2

    private val orientationCircleCenterCoordinates = PointF(radius + margin, radius + margin)
    private val pressureCircleCenterCoordinates = PointF(3 * radius + 2 * margin, radius + margin)
    private val tiltBarStartCoordinates = PointF(5 * radius + 2 * margin, 2 * radius + margin)

    fun DrawScope.drawOrientation(orientation: Float) {

        val ball = calculateBallPosition(orientation)

        // Orientation
        drawCircle(
            Color.LightGray,
            radius,
            Offset(
                orientationCircleCenterCoordinates.x,
                orientationCircleCenterCoordinates.y
            )
        )
        drawCircle(
            Color.DarkGray,
            minorRadius,
            Offset(ball.x, ball.y)
        )
    }

    fun DrawScope.drawTilt(tilt: Float) {
        val tiltBarEndCoordinates = calculateTiltBar(tilt)

        // Tilt
        drawLine(
            Color.DarkGray,
            Offset(tiltBarStartCoordinates.x, tiltBarStartCoordinates.y),
            Offset(tiltBarEndCoordinates.x, tiltBarEndCoordinates.y),
            Stroke.DefaultMiter
        )
    }

    fun DrawScope.drawPressure(pressure: Float) {

        val pressureAlpha = if (pressure < 0.1f) {
            0.1f
        } else if (pressure > 1.0f) {
            1.0f
        } else {
            pressure
        }
        // Pressure
        drawCircle(
            Color(0f, 0f, 0f, pressureAlpha),
            100f,
            Offset(pressureCircleCenterCoordinates.x, pressureCircleCenterCoordinates.y)
        )
    }

    private fun calculateBallPosition(radian: Float): PointF {
        val adjustedR = radius - minorMargin - minorRadius
        var degree = radian * (180 / Math.PI)
        if (degree < 0) {
            degree += 360
        }

        val centerOfCircle = margin + radius

        val x = adjustedR * sin(radian) + centerOfCircle
        val y = adjustedR * cos(radian + PI) + centerOfCircle

        return PointF(x, y.toFloat())
    }

    private fun calculateTiltBar(radian: Float): PointF {
        val x = 2f * radius * sin(radian) + tiltBarStartCoordinates.x
        val y = 2f * radius * cos(radian - PI) + tiltBarStartCoordinates.y

        return PointF(x, y.toFloat())
    }
}