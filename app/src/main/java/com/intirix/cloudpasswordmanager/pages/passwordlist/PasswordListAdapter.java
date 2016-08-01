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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.beans.SessionInfo;

/**
 * Created by jeff on 7/12/16.
 */
public class PasswordListAdapter extends RecyclerView.Adapter<PasswordListViewHolder> {

    private SessionInfo session;

    private Activity activity;

    PasswordListAdapter(Activity activity, SessionInfo session) {
        this.session = session;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        if (session.getPasswordBeanList()==null) {
            return 0;
        }
        return session.getPasswordBeanList().size();
    }

    @Override
    public PasswordListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.password_list_row, parent, false);
        return new PasswordListViewHolder(activity, v);
    }

    @Override
    public void onBindViewHolder(PasswordListViewHolder holder, int position) {
        holder.applyItem(position, session.getPasswordBeanList().get(position));
    }
}
