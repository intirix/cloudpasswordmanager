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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeff on 10/22/16.
 */
public class SavePasswordOptionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.savepassword_option_row_label)
    TextView label;

    @BindView(R.id.savepassword_option_row_descr)
    TextView descr;

    public SavePasswordOptionsViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        ButterKnife.bind(this, itemView);

    }

    void applyItem(int index, SavePasswordOption bean) {
        label.setText(bean.getLabel());
        descr.setText(bean.getDescription());
    }

    @Override
    public void onClick(View v) {

    }
}
