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
import android.os.PersistableBundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.ui.FilterPasswordService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class PasswordListActivity extends SecureActivity implements SearchView.OnQueryTextListener {

    public static final String SAVED_SEARCH_QUERY = "SAVED_SEARCH_QUERY";
    @BindView(R.id.password_list_recycler)
    RecyclerView recyclerView;

    PasswordListAdapter adapter;

    ProgressDialog progressDialog;

    private String filterString = "";

    @Inject
    FilterPasswordService filterPasswordService;

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
            if (savedInstanceState!=null) {
                filterString = savedInstanceState.getString(SAVED_SEARCH_QUERY, "");
            } else {
                filterString = "";
            }

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PasswordListAdapter(this, sessionService.getCurrentSession());
            recyclerView.setAdapter(adapter);
            filter();
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
        filter();
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_SEARCH_QUERY, filterString);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password_list_action, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(filterString, false);

        return super.onCreateOptionsMenu(menu);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordsUpdated(PasswordListUpdatedEvent event) {
        updateProgressDialog();
        if (adapter!=null) {
            filter();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoriesUpdated(CategoryListUpdatedEvent event) {
        updateProgressDialog();
        if (adapter!=null) {
            filter();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterString = query;
        filter();
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterString = newText;
        filter();
        recyclerView.scrollToPosition(0);
        return true;
    }

    private void filter() {
        if (adapter!=null) {
            if (filterString.length() == 0) {
                adapter.refreshFromSession();
            } else {
                adapter.updateList(filterPasswordService.filterPasswords(filterString));
            }
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
