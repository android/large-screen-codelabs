/*
 * Copyright 2023 Google LLC
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

package com.example.activity_embedding

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.window.embedding.SplitAttributes
import androidx.window.embedding.SplitAttributes.SplitType.Companion.SPLIT_TYPE_EXPAND
import androidx.window.embedding.SplitController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView

/**
 * The list portion of a list-detail layout.
 */
class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val listRecyclerView: RecyclerView = findViewById(R.id.listRecyclerView)
        val arraySize = 10
        listRecyclerView.adapter = ItemAdapter(
            Array(arraySize) {
                    i -> if (i == arraySize - 1) "Summary"
                         else "Item ${(i + 1)}"
            }
        )

        SplitController.getInstance(this).setSplitAttributesCalculator {
            params ->

            if (params.areDefaultConstraintsSatisfied) {
                // When default constraints are satisfied, use the navigation rail.
                setWiderScreenNavigation(true)
                return@setSplitAttributesCalculator params.defaultSplitAttributes
            } else {
                // Use the bottom navigation bar in other cases.
                setWiderScreenNavigation(false)
                // Expand containers if the device is in portrait or the width is less than 600 dp.
                SplitAttributes.Builder()
                    .setSplitType(SPLIT_TYPE_EXPAND)
                    .build()
            }
        }
    }

    private fun setWiderScreenNavigation(useNavRail: Boolean) {
        val navRail: NavigationRailView = findViewById(R.id.navigationRailView)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        if (useNavRail) {
            navRail.visibility = View.VISIBLE
            bottomNav.visibility = View.GONE
        } else {
            navRail.visibility = View.GONE
            bottomNav.visibility = View.VISIBLE
        }
    }
}
