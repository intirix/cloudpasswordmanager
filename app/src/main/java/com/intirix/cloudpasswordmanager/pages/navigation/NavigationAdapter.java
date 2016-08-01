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
