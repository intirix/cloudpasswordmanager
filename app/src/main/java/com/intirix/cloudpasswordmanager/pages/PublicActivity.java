package com.intirix.cloudpasswordmanager.pages;

import com.intirix.cloudpasswordmanager.pages.navigation.NavigationItem;

import java.util.LinkedList;

/**
 * Created by jeff on 7/27/16.
 */
public abstract class PublicActivity extends BaseActivity {

    @Override
    protected void addNavigationItems(LinkedList<NavigationItem> navItems) {
        super.addNavigationItems(navItems);
        navItems.addFirst(new LoginNavigationItem(this));

    }

}
