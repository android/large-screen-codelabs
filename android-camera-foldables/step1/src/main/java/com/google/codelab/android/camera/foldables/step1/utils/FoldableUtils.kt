/*
 * Copyright 2022 Google LLC
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
package com.google.codelab.android.camera.foldables.step1.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
//import androidx.window.layout.DisplayFeature

object FoldableUtils {

//    fun getFeaturePositionInViewRect(
//        displayFeature: DisplayFeature,
//        view: View,
//        includePadding: Boolean = true
//    ): Rect? {
//        val viewLocationInWindow = IntArray(2)
//        view.getLocationInWindow(viewLocationInWindow)
//
//        val viewRect = Rect(
//            viewLocationInWindow[0], viewLocationInWindow[1],
//            viewLocationInWindow[0] + view.width, viewLocationInWindow[1] + view.height
//        )
//
//        if (includePadding) {
//            viewRect.left += view.paddingLeft
//            viewRect.top += view.paddingTop
//            viewRect.right -= view.paddingRight
//            viewRect.bottom -= view.paddingBottom
//        }
//
//        val featureRectInView = Rect(displayFeature.bounds)
//        val intersects = featureRectInView.intersect(viewRect)
//        if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
//            !intersects
//        ) {
//            return null
//        }
//        featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1])
//
//        return featureRectInView
//    }

    fun View.moveToRightOf(foldingFeatureRect: Rect) {
        x = foldingFeatureRect.left.toFloat()
        layoutParams = layoutParams.apply {
            width = (parent as View).width - foldingFeatureRect.left
        }
    }

    fun View.moveToTopOf(foldingFeatureRect: Rect) {
        y = 0f
        layoutParams = layoutParams.apply {
            height = foldingFeatureRect.top
        }
    }

    fun View.restore() {
        layoutParams = layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        y = 0f
        x = 0f
    }
}