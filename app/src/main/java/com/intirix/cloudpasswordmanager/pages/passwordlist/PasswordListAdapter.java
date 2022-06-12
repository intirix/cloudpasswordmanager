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

import android.app.Activity;

import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jeff on 7/12/16.
 */
public class PasswordListAdapter extends RecyclerView.Adapter<PasswordListViewHolder> {

    private static final String TAG = PasswordListAdapter.class.getSimpleName();

    private SessionInfo session;

    private Activity activity;

    private final SortedList<PasswordBean> sortedList = new SortedList<PasswordBean>(PasswordBean.class, new PasswordListSortedCallback(this));

    PasswordListAdapter(Activity activity, SessionInfo session) {
        this.session = session;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    /**
     * Refresh from the password list
     */
    public void refreshFromSession() {
        List<PasswordBean> newList = session.getPasswordBeanList();
        updateList(newList);
    }

    /**
     * Update the list of passwords
     * @param newList
     */
    public void updateList(List<PasswordBean> newList) {
        sortedList.beginBatchedUpdates();
        final long startingSize = sortedList.size();
        if (newList==null||newList.size()==0) {
            sortedList.clear();
            Log.d(TAG,"Clearing the list adapter");
        } else {
            Set<PasswordBean> toBeAdded = new HashSet<>(newList);

            for (int i = sortedList.size() - 1; i >= 0; i--) {
                final PasswordBean model = sortedList.get(i);
                if (!newList.contains(model)) {
                    Log.d(TAG,"Removing item from password list: "+model.getId());
                    sortedList.remove(model);
                } else if (toBeAdded.contains(model)) {
                    Log.d(TAG,"Adding item to password list: "+model.getId());
                    // de-duplicate
                    toBeAdded.remove(model);
                }
            }
            for (final PasswordBean bean: toBeAdded) {
                try {
                    sortedList.add((PasswordBean)bean.clone());
                } catch (CloneNotSupportedException e) {
                    // ignore
                }
            }
            Log.d(TAG,"Password list now has "+sortedList.size()+" items");
        }
        sortedList.endBatchedUpdates();
        final long endingSize = sortedList.size();
        Log.d(TAG,"List adapter went from "+startingSize+" to "+endingSize);

    }

    @Override
    public PasswordListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.password_list_row, parent, false);
        return new PasswordListViewHolder(activity, v);
    }

    @Override
    public void onBindViewHolder(PasswordListViewHolder holder, int position) {
        holder.applyItem(position, sortedList.get(position));
    }
}
