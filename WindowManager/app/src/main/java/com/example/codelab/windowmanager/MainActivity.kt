/*
 *
 *  * Copyright (C) 2021 Google Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.example.codelab.windowmanager

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetricsCalculator
import com.example.codelab.windowmanager.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var windowInfoTracker: WindowInfoTracker
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowInfoTracker = WindowInfoTracker.getOrCreate(this@MainActivity)

        obtainWindowMetrics()
        onWindowLayoutInfoChange()
    }

    private fun obtainWindowMetrics() {
        val wmc = WindowMetricsCalculator.getOrCreate()
        val currentWM = wmc.computeCurrentWindowMetrics(this).bounds.flattenToString()
        val maximumWM = wmc.computeMaximumWindowMetrics(this).bounds.flattenToString()
        binding.windowMetrics.text =
            "CurrentWindowMetrics: ${currentWM}\nMaximumWindowMetrics: ${maximumWM}"
    }

    private fun onWindowLayoutInfoChange() {
        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowInfoTracker.windowLayoutInfo(this@MainActivity)
                    .collect { value ->
                        updateUI(value)
                    }
            }
        }
    }

    private fun updateUI(newLayoutInfo: WindowLayoutInfo) {
        binding.layoutChange.text = newLayoutInfo.toString()
        if (newLayoutInfo.displayFeatures.isNotEmpty()) {
            binding.configurationChanged.text = "Spanned across displays"
            alignViewToFoldingFeatureBounds(newLayoutInfo)
        } else {
            binding.configurationChanged.text = "One logic/physical display - unspanned"
        }
    }

    private fun alignViewToFoldingFeatureBounds(newLayoutInfo: WindowLayoutInfo) {
        val constraintLayout = binding.constraintLayout
        val set = ConstraintSet()
        set.clone(constraintLayout)

        // Get and translate the feature bounds to the View's coordinate space and current
        // position in the window.
        val foldingFeature = newLayoutInfo.displayFeatures[0] as FoldingFeature
        val bounds = getFeatureBoundsInWindow(foldingFeature, binding.root)

        bounds?.let { rect ->
            // Some devices have a 0px width folding feature. We set a minimum of 1px so we
            // can show the view that mirrors the folding feature in the UI and use it as reference.
            val horizontalFoldingFeatureHeight = (rect.bottom - rect.top).coerceAtLeast(1)
            val verticalFoldingFeatureWidth = (rect.right - rect.left).coerceAtLeast(1)

            // Sets the view to match the height and width of the folding feature
            set.constrainHeight(
                R.id.folding_feature,
                horizontalFoldingFeatureHeight
            )
            set.constrainWidth(
                R.id.folding_feature,
                verticalFoldingFeatureWidth
            )

            set.connect(
                R.id.folding_feature, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START, 0
            )
            set.connect(
                R.id.folding_feature, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0
            )

            if (foldingFeature.orientation == FoldingFeature.Orientation.VERTICAL) {
                set.setMargin(R.id.folding_feature, ConstraintSet.START, rect.left)
                set.connect(
                    R.id.layout_change, ConstraintSet.END,
                    R.id.folding_feature, ConstraintSet.START, 0
                )
            } else {
                // FoldingFeature is Horizontal
                set.setMargin(
                    R.id.folding_feature, ConstraintSet.TOP,
                    rect.top
                )
                set.connect(
                    R.id.layout_change, ConstraintSet.TOP,
                    R.id.folding_feature, ConstraintSet.BOTTOM, 0
                )
            }

            // Set the view to visible and apply constraints
            set.setVisibility(R.id.folding_feature, View.VISIBLE)
            set.applyTo(constraintLayout)
        }
    }

    /**
     * Get the bounds of the display feature translated to the View's coordinate space and current
     * position in the window. This will also include view padding in the calculations.
     */
    private fun getFeatureBoundsInWindow(
        displayFeature: DisplayFeature,
        view: View,
        includePadding: Boolean = true
    ): Rect? {
        // Adjust the location of the view in the window to be in the same coordinate space as the feature.
        val viewLocationInWindow = IntArray(2)
        view.getLocationInWindow(viewLocationInWindow)

        // Intersect the feature rectangle in window with view rectangle to clip the bounds.
        val viewRect = Rect(
            viewLocationInWindow[0], viewLocationInWindow[1],
            viewLocationInWindow[0] + view.width, viewLocationInWindow[1] + view.height
        )

        // Include padding if needed
        if (includePadding) {
            viewRect.left += view.paddingLeft
            viewRect.top += view.paddingTop
            viewRect.right -= view.paddingRight
            viewRect.bottom -= view.paddingBottom
        }

        val featureRectInView = Rect(displayFeature.bounds)
        val intersects = featureRectInView.intersect(viewRect)

        // Checks to see if the display feature overlaps with our view at all
        if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
            !intersects
        ) {
            return null
        }

        // Offset the feature coordinates to view coordinate space start point
        featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1])

        return featureRectInView
    }
}
