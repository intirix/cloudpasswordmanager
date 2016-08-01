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
package com.intirix.cloudpasswordmanager.pages.navigation;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by jeff on 7/28/16.
 */
public class NavigationClickListener implements ListView.OnItemClickListener {
    private Activity activity;

    private DrawerLayout drawerLayout;

    private NavigationAdapter adapter;

    private static final int OFFSET = 1;

    public NavigationClickListener(Activity activity, DrawerLayout drawerLayout, NavigationAdapter adapter) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.adapter = adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position>(OFFSET-1)) {
            adapter.getItem(position-OFFSET).onClick(activity);
            drawerLayout.closeDrawers();
        }
    }
}
