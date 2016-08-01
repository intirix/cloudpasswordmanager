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
