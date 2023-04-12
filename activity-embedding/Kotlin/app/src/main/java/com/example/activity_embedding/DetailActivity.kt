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

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView

/**
 * The detail portion of a list-detail layout.
 *
 * The content related to a list item.
 */
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_detail)

      supportActionBar?.setDisplayHomeAsUpEnabled(true)

      val textView: TextView = findViewById(R.id.textViewItemDetail)

      if (intent?.action == Intent.ACTION_VIEW && intent.type == "text/plain") {
          textView.text = intent.getStringExtra(Intent.EXTRA_TEXT)
      }
    }

    /**
     * Handles selection of the up/home control.
     *
     * @param item The selected options menu item.
     * @return The return value of the super method.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finishActivity()
        return super.onOptionsItemSelected(item)
    }

  /**
   * Finishes this activity.
   */
  private fun finishActivity() { finish() }

}