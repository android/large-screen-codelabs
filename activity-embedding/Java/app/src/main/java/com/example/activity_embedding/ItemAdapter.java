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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for the list of items in the primary container of the activity
 * embedding split.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    /** The RecyclerView items. */
    private final String[] itemList;

    /**
     * Creates the ItemAdapter. Initialize the adapter data set.
     *
     * @param itemList An array of items to be added to the item list.
     */
    public ItemAdapter(String[] itemList) {
        this.itemList = itemList;
    }

    /**
     * An individual item in the item list.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        /** An inflated layout which is a list item. */
        private final View listItem;

        /** A TextView contained in the list item layout file. */
        private final TextView listItemTextView;

        /**
         * Creates a reference to a list item.
         *
         * @param listItem A `View` that becomes the list item.
         */
        public ItemViewHolder(View listItem) {
            super(listItem);
            this.listItem = listItem;
            listItemTextView = listItem.findViewById(R.id.list_item);
        }
    }

    /**
     * Creates a list item from a layout resource file.
     *
     * @param parent The container of the new view holder.
     * @param viewType The type of the new view.
     * @return The list item as an `ItemViewHolder`.
     */
    @NonNull
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    /**
     * Adds an item to the list of displayable items.
     *
     * @param itemViewHolder The item to be added to the list.
     * @param position The zero-based index of the item in list.
     */
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.listItemTextView.setText(itemList[position]);

        itemViewHolder.listItem.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent;
            if (position == getItemCount() - 1)
                intent = new Intent(context, SummaryActivity.class);
            else
                intent = new Intent(context, DetailActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(Intent.EXTRA_TEXT, itemList[position] + " detail");
            intent.setType("text/plain");
            context.startActivity(intent);
        });
    }

    /**
     * Gets the number of items in the list.
     *
     * @return The number of list items.
     */
    public int getItemCount() {
        return itemList.length;
    }

}