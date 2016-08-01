package com.intirix.cloudpasswordmanager.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.navigation.NavigationItem;

/**
 * Created by jeff on 7/31/16.
 */
public class LoginNavigationItem extends NavigationItem {

    public LoginNavigationItem(Context context) {
        super(context);
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.login_nav_label);
    }

    @Override
    public void onClick(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);

    }
}
