package com.intirix.cloudpasswordmanager.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.navigation.NavigationItem;
import com.intirix.cloudpasswordmanager.services.SessionService;

/**
 * Created by jeff on 7/31/16.
 */
public class LogOffNavigationItem extends NavigationItem {
    private SessionService sessionService;

    public LogOffNavigationItem(Context context, SessionService sessionService) {
        super(context);
        this.sessionService = sessionService;
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.logout_nav_label);
    }

    @Override
    public void onClick(Activity activity) {
        sessionService.end();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }
}
