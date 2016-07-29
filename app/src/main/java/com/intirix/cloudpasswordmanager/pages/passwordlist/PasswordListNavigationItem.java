package com.intirix.cloudpasswordmanager.pages.passwordlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.intirix.cloudpasswordmanager.pages.navigation.NavigationItem;

/**
 * Created by jeff on 7/28/16.
 */
public class PasswordListNavigationItem extends NavigationItem {
    public PasswordListNavigationItem(Context context) {
        super(context);
    }

    @Override
    public String getLabel(Context context) {
        return "Password List";
    }

    @Override
    public void onClick(Activity activity) {
        Intent intent = new Intent(activity, PasswordListActivity.class);
        activity.startActivity(intent);

    }
}
