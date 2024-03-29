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
package com.intirix.cloudpasswordmanager.pages;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.about.AboutNavigationItem;
import com.intirix.cloudpasswordmanager.pages.login.LogOffNavigationItem;
import com.intirix.cloudpasswordmanager.pages.login.LoginNavigationItem;
import com.intirix.cloudpasswordmanager.pages.navigation.NavigationAdapter;
import com.intirix.cloudpasswordmanager.pages.navigation.NavigationClickListener;
import com.intirix.cloudpasswordmanager.pages.navigation.NavigationItem;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListNavigationItem;
import com.intirix.cloudpasswordmanager.pages.settings.SettingsNavigationItem;
import com.intirix.cloudpasswordmanager.services.session.AutoLogoffService;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import java.util.LinkedList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeff on 7/27/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private NavigationAdapter adapter;

    protected abstract int getLayoutId();

    protected FrameLayout contentFrame;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.left_drawer)
    ListView drawerListView;

    private ActionBarDrawerToggle drawerToggle;

    @Inject
    protected AutoLogoffService autoLogoffService;

    @Inject
    protected SessionService sessionService;

    protected boolean twoPane = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the base layout
        setContentView(R.layout.base);
        contentFrame = (FrameLayout)findViewById(R.id.content_frame);

        // attach the activity's actual layout to the page
        getLayoutInflater().inflate(getLayoutId(), contentFrame, true);

        if (findViewById(R.id.frame_main)!=null) {
            if (savedInstanceState==null) {
                Log.d(TAG, "Layout has a fragment frame, creating initial fragment");
                Fragment frag = createInitialFragment();
                if (frag!=null) {
                    Log.d(TAG, "Adding fragment "+frag.getClass().getSimpleName());

                    frag.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_main, frag).commit();
                } else {
                    Log.d(TAG, "No fragment created");
                }
            } else {
                Log.d(TAG, "Layout has a fragment frame, restoring previous fragment");
            }
        }


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ButterKnife.bind(this);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                myToolbar, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        drawerToggle.syncState();

        drawerListView.addHeaderView(getLayoutInflater().inflate(R.layout.drawer_header, drawerListView, false));

        PasswordApplication.getSInjector(this).inject(this);

    }

    /**
     * Create the initial fragment
     * @return
     */
    protected Fragment createInitialFragment() {
        return null;
    }



    protected void addNavigationItems(LinkedList<NavigationItem> navItems) {
        if (autoLogoffService.isSessionStillValid()) {
            navItems.addFirst(new LogOffNavigationItem(this, sessionService));
            navItems.addLast(new PasswordListNavigationItem(this));
            navItems.addLast(new SettingsNavigationItem(this));

        } else {
            navItems.addFirst(new LoginNavigationItem(this));
        }
        navItems.addLast(new AboutNavigationItem(this));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        final LinkedList<NavigationItem> navItems = new LinkedList<>();
        addNavigationItems(navItems);
        adapter = new NavigationAdapter(this, navItems);
        drawerListView.setAdapter(adapter);
        drawerListView.setOnItemClickListener(new NavigationClickListener(this, drawerLayout, adapter));


        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    public ListView getDrawerListView() {
        return drawerListView;
    }


    /**
     * Change the right pane
     * @param frag
     */
    public void navigateRightPane(Fragment frag) {
        if (twoPane) {
            Log.d(TAG, "Navigating right pane to fragment "+frag.getClass().getSimpleName());

        } else {
            Log.d(TAG, "Navigating page to fragment "+frag.getClass().getSimpleName());
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, frag).addToBackStack(null).commit();
        }
    }
}
