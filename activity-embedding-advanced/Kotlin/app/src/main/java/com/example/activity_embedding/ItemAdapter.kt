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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for the list of items in the primary container of the activity
 * embedding split.
 *
 * @param itemList An array of items to be added to the item list.
 */
class ItemAdapter(private val itemList: Array<String>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    /**
     * An individual item in the item list.
     *
     * @param listItem A `View` that becomes the list item.
     */
    class ItemViewHolder(listItem: View) : RecyclerView.ViewHolder(listItem) {
        val listItem: ViewGroup = listItem as ViewGroup
        val listItemTextView: TextView = listItem.findViewById(R.id.list_item)
    }

    /**
     * Creates a list item from a layout resource file.
     *
     * @param parent The container of the new view holder.
     * @param viewType The type of the new view.
     * @return The list item as an `ItemViewHolder`.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    /**
     * Adds an item to the list of displayable items.
     *
     * @param itemViewHolder The item to be added to the list.
     * @param position The zero-based index of the item in list.
     */
    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int) {
        itemViewHolder.listItemTextView.text = itemList[position]

        itemViewHolder.listItem.setOnClickListener { v ->
            val context = v.context
            val intent = if (position == itemCount - 1)
                Intent(context, SummaryActivity::class.java)
                else Intent(context, DetailActivity::class.java)
            intent.apply {
                action = Intent.ACTION_VIEW
                putExtra(Intent.EXTRA_TEXT,"${itemList[position]} detail")
                type = "text/plain"
            }
            context.startActivity(intent)
        }

    }

    /**
     * Gets the number of items in the list.
     *
     * @return The number of list items.
     */
    override fun getItemCount(): Int {
        return itemList.size
    }
}