package com.intirix.cloudpasswordmanager.pages.navigation;

import android.app.Activity;
import android.content.Context;

/**
 * Created by jeff on 7/28/16.
 */
public abstract class NavigationItem {

    private String label;

    public NavigationItem(Context context) {
        label = getLabel(context);
    }

    /**
     * Handles the on click event
     * @param activity
     */
    public abstract void onClick(Activity activity);

    /**
     * Get the label for the menu item
     * @param context
     * @return
     */
    public abstract String getLabel(Context context);

    @Override
    public String toString() {
        return label;
    }
}
