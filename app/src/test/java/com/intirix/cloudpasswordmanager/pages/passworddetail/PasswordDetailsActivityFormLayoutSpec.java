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
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.backend.MockPasswordRequestService;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricTestRunner.class)


public class PasswordDetailsActivityFormLayoutSpec extends BaseTestCase {

    @Test
    public void verifyShareListSaysNoneWhenNotShared() {
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
        bean.addSharedUsers("myuser");

        passwords.add(bean);

        sessionService.start();
        sessionService.setUsername("myuser");
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_ID, bean.getId());

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class,intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        MockPasswordRequestService passwordRequestService = new MockPasswordRequestService();
        activity.passwordRequestService = passwordRequestService;

        passwordRequestService.setSupportSharing(true);
        activity.updateForm();

        Assert.assertEquals("None", activity.shareValue.getText().toString());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyShareListSaysNoneWhenUnknownSharing() {
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

        // make sure it is empty
        bean.clearSharedUsers();

        passwords.add(bean);

        sessionService.start();
        sessionService.setUsername("myuser");
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_ID, bean.getId());

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class,intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        MockPasswordRequestService passwordRequestService = new MockPasswordRequestService();
        activity.passwordRequestService = passwordRequestService;

        passwordRequestService.setSupportSharing(true);
        activity.updateForm();

        Assert.assertEquals("None", activity.shareValue.getText().toString());

        controller.pause().stop().destroy();
    }




    @Test
    public void verifyShareListSaysOneWhenShared() {
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
        bean.addSharedUsers("myuser","otheruser");

        passwords.add(bean);

        sessionService.start();
        sessionService.setUsername("myuser");
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_ID, bean.getId());

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class,intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        MockPasswordRequestService passwordRequestService = new MockPasswordRequestService();
        activity.passwordRequestService = passwordRequestService;

        passwordRequestService.setSupportSharing(true);
        activity.updateForm();

        Assert.assertEquals("1", activity.shareValue.getText().toString());

        controller.pause().stop().destroy();
    }



}
