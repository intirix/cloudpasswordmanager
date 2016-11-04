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
package com.intirix.cloudpasswordmanager.pages.settings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intirix.cloudpasswordmanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 10/22/16.
 */
public class SavePasswordOptionsAdapter extends RecyclerView.Adapter<SavePasswordOptionsViewHolder> {

    private List<SavePasswordOption> options = new ArrayList<>();

    public void addOption(Context ctx, SavePasswordOption option) {
        options.add(option);
    }

    @Override
    public SavePasswordOptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_savepassword_option_row, parent, false);
        return new SavePasswordOptionsViewHolder(v);

    }

    @Override
    public void onBindViewHolder(SavePasswordOptionsViewHolder holder, int position) {
        holder.applyItem(position, options.get(position));

    }

    @Override
    public int getItemCount() {
        return options.size();
    }
}
