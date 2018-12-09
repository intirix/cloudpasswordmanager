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
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.backend.MockPasswordRequestService;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricTestRunner.class)


public class PasswordDetailsActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyBaseLayout() throws Exception {
        SessionService sessionService = serviceRef.sessionService();

        List<PasswordBean> passwords = new ArrayList<>();

        PasswordBean bean = new PasswordBean();

        bean.setWebsite("www.facebook.com");
        bean.setId("4324");
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
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_ID, bean.getId());

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class,intent).create().start();
        PasswordDetailActivity activity = controller.get();
        MockPasswordRequestService passwordRequestService = new MockPasswordRequestService();
        activity.passwordRequestService = passwordRequestService;
        controller.resume();


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

    @Test
    public void verifyShareButtonVisibleWhenSharingAvailable() {
        SessionService sessionService = serviceRef.sessionService();

        List<PasswordBean> passwords = new ArrayList<>();

        PasswordBean bean = new PasswordBean();

        bean.setWebsite("www.facebook.com");
        bean.setId("4324");
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
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_ID, bean.getId());

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class,intent).create().start();
        PasswordDetailActivity activity = controller.get();
        MockPasswordRequestService passwordRequestService = new MockPasswordRequestService();
        activity.passwordRequestService = passwordRequestService;
        controller.resume();

        passwordRequestService.setSupportSharing(true);
        activity.updateForm();

        Assert.assertEquals(View.VISIBLE,activity.findViewById(R.id.password_detail_share).getVisibility());
        Assert.assertEquals(View.VISIBLE,activity.findViewById(R.id.password_detail_share_label).getVisibility());
        Assert.assertEquals(View.VISIBLE,activity.findViewById(R.id.password_detail_share_value).getVisibility());


        controller.pause().stop().destroy();
    }

    @Test
    public void verifyShareButtonNotVisibleWhenSharingUnavailable() {
        SessionService sessionService = serviceRef.sessionService();

        List<PasswordBean> passwords = new ArrayList<>();

        PasswordBean bean = new PasswordBean();

        bean.setWebsite("www.facebook.com");
        bean.setId("4324");
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
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_ID, bean.getId());

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class,intent).create().start();
        PasswordDetailActivity activity = controller.get();
        MockPasswordRequestService passwordRequestService = new MockPasswordRequestService();
        activity.passwordRequestService = passwordRequestService;
        controller.resume();

        passwordRequestService.setSupportSharing(false);
        activity.updateForm();

        Assert.assertNotEquals(View.VISIBLE,activity.findViewById(R.id.password_detail_share).getVisibility());
        Assert.assertNotEquals(View.VISIBLE,activity.findViewById(R.id.password_detail_share_label).getVisibility());
        Assert.assertNotEquals(View.VISIBLE,activity.findViewById(R.id.password_detail_share_value).getVisibility());


        controller.pause().stop().destroy();
    }

}
