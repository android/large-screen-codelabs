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

package com.example.activity_embedding;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.embedding.SplitAttributes;
import androidx.window.embedding.SplitController;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

/**
 * The list portion of a list-detail layout.
 */
public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView listRecyclerView = findViewById(R.id.listRecyclerView);

        String[] itemList = new String[10];
        for (int i = 0; i < itemList.length; i++) {
            if (i == itemList.length - 1) itemList[i] = "Summary";
            else itemList[i] = "Item " + (i + 1);
        }

        listRecyclerView.setAdapter(new ItemAdapter(itemList));

        SplitController.getInstance(this).setSplitAttributesCalculator(params -> {
            if (params.areDefaultConstraintsSatisfied()) {
                // When default constraints are satisfied, use the navigation rail.
                setWiderScreenNavigation(true);
                return params.getDefaultSplitAttributes();
            } else {
                // Use the bottom navigation bar in other cases.
                setWiderScreenNavigation(false);
                // Expand containers if the device is in portrait or the width is less than 600 dp.
                return new SplitAttributes.Builder()
                        .setSplitType(SplitAttributes.SplitType.SPLIT_TYPE_EXPAND)
                        .build();
            }
        });
    }

    private void setWiderScreenNavigation(boolean useNavRail) {
        NavigationRailView navRail = findViewById(R.id.navigationRailView);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (useNavRail) {
            navRail.setVisibility(View.VISIBLE);
            bottomNav.setVisibility(View.GONE);
        } else {
            navRail.setVisibility(View.GONE);
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

}
