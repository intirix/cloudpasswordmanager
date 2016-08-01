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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.events.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.events.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

public class PasswordListActivity extends SecureActivity {

    @BindView(R.id.password_list_recycler)
    RecyclerView recyclerView;

    PasswordListAdapter adapter;

    ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.password_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PasswordApplication.getSInjector(this).inject(this);

        // if the session has ended, then send us back to the logon page
        if (sessionService.getCurrentSession()!=null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PasswordListAdapter(this, sessionService.getCurrentSession());
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateProgressDialog();
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password_list_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordsUpdated(PasswordListUpdatedEvent event) {
        updateProgressDialog();
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoriesUpdated(CategoryListUpdatedEvent event) {
        updateProgressDialog();
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateProgressDialog() {
        if (sessionService.getCurrentSession()!=null) {
            boolean showDialog = sessionService.getCurrentSession().getCategoryList()==null||sessionService.getCurrentSession().getPasswordList()==null;

            if (showDialog) {
                if (progressDialog==null) {
                    progressDialog = ProgressDialog.show(this, "", getString(R.string.password_list_progress_message));
                } else {
                    progressDialog.show();
                }
            } else if (progressDialog!=null) {
                progressDialog.dismiss();
            }
        }
    }
}
