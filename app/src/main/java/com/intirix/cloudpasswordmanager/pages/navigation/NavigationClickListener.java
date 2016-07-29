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
