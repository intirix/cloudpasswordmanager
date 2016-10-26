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
package com.intirix.cloudpasswordmanager.pages.settings;

import android.support.v7.widget.RecyclerView;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class SettingsActivityActionSpec extends BaseTestCase {


    @Test
    public void verifySavePasswordOptions() throws Exception {
        SessionService sessionService = serviceRef.sessionService();


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create().start().resume();
        SettingsActivity activity = controller.get();


        Assert.assertEquals("Settings", activity.getTitle().toString());
        Assert.assertNotNull(activity.findViewById(R.id.settings_savepass_label));

        activity.findViewById(R.id.settings_savepass_value).performClick();

        Assert.assertNotNull(activity.findViewById(R.id.settings_savepass_options_recycler));

        RecyclerView rv = (RecyclerView)activity.findViewById(R.id.settings_savepass_options_recycler);

        // verify that there is an entry in the list
        Assert.assertEquals(2, rv.getAdapter().getItemCount());

        rv.measure(0,0);
        rv.layout(0,0,100,1000);
        Shadows.shadowOf(rv).dump();


        controller.pause().stop().destroy();
    }


}
