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
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;
import com.intirix.cloudpasswordmanager.pages.passwordadd.PasswordAddActivity;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.ui.FilterPasswordService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class PasswordListActivity extends SecureActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = PasswordListActivity.class.getSimpleName();

    public static final String SAVED_SEARCH_QUERY = "SAVED_SEARCH_QUERY";
    @BindView(R.id.password_list_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.password_list_add_button)
    FloatingActionButton addButton;

    PasswordListAdapter adapter;

    ProgressDialog progressDialog;

    private String filterString = "";

    @Inject
    FilterPasswordService filterPasswordService;

    @Inject
    PasswordRequestService passwordRequestService;

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
    protected void onResume() {
        super.onResume();

        if (passwordRequestService.backendSupportsAddingPassword()) {
            addButton.show();
        } else {
            addButton.hide();
        }

        updateProgressDialog();
        filter();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // prevent window leak if we go home while spinner is up
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }
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
        final SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(filterString, false);

        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.password_list_add_button)
    public void onAddPassword(View view) {
        Intent intent = new Intent(this, PasswordAddActivity.class);
        startActivity(intent);
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
            boolean categoriesDecrypted = sessionService.getCurrentSession().getCategoryList() != null;
            boolean passwordsDecrypted = sessionService.getCurrentSession().getPasswordBeanList() != null;
            Log.d(TAG,"Categories decrypted: "+categoriesDecrypted);
            Log.d(TAG,"Passwords decrypted: "+passwordsDecrypted);
            boolean showDialog = !categoriesDecrypted || !passwordsDecrypted;

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
