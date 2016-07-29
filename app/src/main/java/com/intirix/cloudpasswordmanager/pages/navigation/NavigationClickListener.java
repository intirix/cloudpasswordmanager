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

    public NavigationClickListener(Activity activity, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NavigationAdapter adapter = (NavigationAdapter)parent.getAdapter();
        adapter.getItem(position).onClick(activity);
        drawerLayout.closeDrawers();
    }
}
