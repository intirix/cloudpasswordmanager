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
package com.intirix.cloudpasswordmanager.pages.passworddetail;

import android.content.Intent;
import android.view.View;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.SessionService;
import com.intirix.cloudpasswordmanager.services.beans.PasswordBean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class PasswordDetailsActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyBaseLayout() throws Exception {
        SessionService sessionService = serviceRef.sessionService();

        List<PasswordBean> passwords = new ArrayList<>();

        PasswordBean bean = new PasswordBean();

        bean.setWebsite("www.facebook.com");
        bean.setLoginName("markz");
        bean.setPass("ABCD!@#$");
        bean.setHasLower(false);
        bean.setHasUpper(true);
        bean.setHasNumber(false);
        bean.setHasSpecial(true);
        bean.setCategoryName("Social");
        bean.setNotes("My facebook login");

        passwords.add(bean);

        sessionService.start();
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();


        Assert.assertEquals("Password Details", activity.getTitle().toString());
        Assert.assertEquals(bean.getWebsite(), activity.website.getText().toString());
        Assert.assertEquals(bean.getLoginName(), activity.username.getText().toString());
        Assert.assertEquals("********{"+bean.getPass().length()+'}', activity.password.getText().toString());
        Assert.assertFalse(activity.passwordContainsLower.isEnabled());
        Assert.assertFalse(activity.passwordContainsNumber.isEnabled());
        Assert.assertTrue(activity.passwordContainsSpecial.isEnabled());
        Assert.assertTrue(activity.passwordContainsUpper.isEnabled());
        Assert.assertNotNull(activity.passwordCopyAction);
        Assert.assertEquals(View.INVISIBLE, activity.passwordHideAction.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.passwordShowAction.getVisibility());
        Assert.assertEquals(bean.getCategoryName(), activity.category.getText().toString());
        Assert.assertEquals(bean.getNotes(), activity.notes.getText().toString());

        controller.pause().stop().destroy();
    }


}
