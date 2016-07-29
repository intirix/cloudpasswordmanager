package com.intirix.cloudpasswordmanager.pages.navigation;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.intirix.cloudpasswordmanager.R;

import java.util.List;

/**
 * Created by jeff on 7/28/16.
 */
public class NavigationAdapter extends ArrayAdapter<NavigationItem> {
    public NavigationAdapter(Context context, List<NavigationItem> objects) {
        super(context, R.layout.drawer_list_item, objects);
    }
}
