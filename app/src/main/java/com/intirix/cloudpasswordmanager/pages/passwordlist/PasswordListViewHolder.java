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
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordDetailActivity;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeff on 7/12/16.
 */
public class PasswordListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Activity activity;

    private int index;

    private String id;

    @BindView(R.id.password_list_row_website)
    TextView website;

    @BindView(R.id.password_list_row_loginName)
    TextView loginName;

    @BindView(R.id.password_list_row_category)
    TextView categoryName;

    public PasswordListViewHolder(Activity activity, View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.activity = activity;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, PasswordDetailActivity.class);
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_ID, id);
        activity.startActivity(intent);
    }

    void applyItem(int index, PasswordBean pass) {
        this.index = index;
        this.id = pass.getId();
        website.setText(pass.getWebsite());

        if (pass.isDecrypted()) {
            loginName.setText(pass.getLoginName());
            if (pass.getCategoryName() == null || pass.getCategoryName().length() == 0) {
                categoryName.setVisibility(View.GONE);
            } else {
                categoryName.setVisibility(View.VISIBLE);
                categoryName.setText(pass.getCategoryName());
                categoryName.setTextColor(pass.getCategoryForeground());


                if (categoryName.getBackground() != null && categoryName.getBackground() instanceof PaintDrawable) {
                    PaintDrawable bg = (PaintDrawable) categoryName.getBackground();
                    bg.getPaint().setColor(pass.getCategoryBackground());
                } else {
                    PaintDrawable bg = new PaintDrawable(pass.getCategoryBackground());
                    bg.setCornerRadius(16);
                    categoryName.setBackgroundDrawable(bg);
                }
            }
        } else {
            categoryName.setVisibility(View.VISIBLE);
            categoryName.setText(activity.getString(R.string.password_list_decryption_failed));
            categoryName.setTextColor(activity.getResources().getColor(android.R.color.white));

            if (categoryName.getBackground() != null && categoryName.getBackground() instanceof PaintDrawable) {
                PaintDrawable bg = (PaintDrawable) categoryName.getBackground();
                bg.getPaint().setColor(activity.getResources().getColor(R.color.categoryDecryptionFailed));
            } else {
                PaintDrawable bg = new PaintDrawable(activity.getResources().getColor(R.color.categoryDecryptionFailed));
                bg.setCornerRadius(16);
                categoryName.setBackgroundDrawable(bg);
            }

        }
    }
}
