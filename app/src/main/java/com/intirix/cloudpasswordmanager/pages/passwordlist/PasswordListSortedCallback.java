/*
 * Copyright (C) 2016 Jeff Mercer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intirix.cloudpasswordmanager.pages.passwordlist;

import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.RecyclerView;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

/**
 * Created by jeff on 8/17/16.
 */
public class PasswordListSortedCallback extends SortedList.Callback<PasswordBean> {

    private RecyclerView.Adapter adapter;

    public PasswordListSortedCallback(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int compare(PasswordBean o1, PasswordBean o2) {
        return o1.compareTo(o2);
    }

    @Override
    public void onInserted(int position, int count) {
        adapter.notifyItemRangeInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        adapter.notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count) {
        adapter.notifyItemRangeChanged(position, count);
    }

    @Override
    public boolean areContentsTheSame(PasswordBean oldItem, PasswordBean newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areItemsTheSame(PasswordBean item1, PasswordBean item2) {
        return item1==item2;
    }
}
