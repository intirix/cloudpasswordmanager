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
package com.intirix.cloudpasswordmanager.pages.passwordlist;

import android.graphics.drawable.PaintDrawable;
import android.view.View;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class PasswordListActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyBaseLayout() throws Exception {
        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertNotNull(activity.recyclerView);

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyRowLayoutWithCategory() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        PasswordBean pass1 = new PasswordBean();
        pass1.setWebsite("www.gmail.com");
        pass1.setLoginName("myGmailUsername");
        pass1.setCategory("5");
        pass1.setCategoryName("Finance");
        pass1.setCategoryForeground(0xFF00FF00);
        pass1.setCategoryBackground(0xFFFFFFFF);
        List<PasswordBean> passwords = new ArrayList<>();
        passwords.add(pass1);
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        // verify that there is an entry in the list
        Assert.assertEquals(1, activity.adapter.getItemCount());

        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        PasswordListViewHolder vh = activity.adapter.onCreateViewHolder(activity.recyclerView, 0);
        activity.adapter.onBindViewHolder(vh, 0);
        Assert.assertEquals(pass1.getWebsite(), vh.website.getText().toString());
        Assert.assertEquals(pass1.getLoginName(), vh.loginName.getText().toString());
        Assert.assertEquals(View.VISIBLE, vh.categoryName.getVisibility());
        Assert.assertEquals(pass1.getCategoryName(), vh.categoryName.getText().toString());
        Assert.assertEquals(pass1.getCategoryForeground(), vh.categoryName.getCurrentTextColor());
        // this assumes a certain Android implementation
        Assert.assertEquals(pass1.getCategoryBackground(), ((PaintDrawable)vh.categoryName.getBackground()).getPaint().getColor());

        controller.pause().stop().destroy();

    }

    @Test
    public void verifyRowLayoutWithoutCategory() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        PasswordBean pass1 = new PasswordBean();
        pass1.setWebsite("www.gmail.com");
        pass1.setLoginName("myGmailUsername");
        List<PasswordBean> passwords = new ArrayList<>();
        passwords.add(pass1);
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        // verify that there is an entry in the list
        Assert.assertEquals(1, activity.adapter.getItemCount());

        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        PasswordListViewHolder vh = activity.adapter.onCreateViewHolder(activity.recyclerView, 0);
        activity.adapter.onBindViewHolder(vh, 0);
        Assert.assertEquals(pass1.getWebsite(), vh.website.getText().toString());
        Assert.assertEquals(pass1.getLoginName(), vh.loginName.getText().toString());
        Assert.assertEquals(View.GONE, vh.categoryName.getVisibility());

        controller.pause().stop().destroy();

    }

}
