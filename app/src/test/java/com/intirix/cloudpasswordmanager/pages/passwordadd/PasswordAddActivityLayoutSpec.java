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
package com.intirix.cloudpasswordmanager.pages.passwordadd;

import android.content.Intent;
import android.view.View;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordDetailActivity;
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
 * Created by jeff on 11/21/2018.
 */
@RunWith(RobolectricTestRunner.class)
public class PasswordAddActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyBaseLayout() throws Exception {
        SessionService sessionService = serviceRef.sessionService();

        sessionService.start();

        ActivityController<PasswordAddActivity> controller = Robolectric.buildActivity(PasswordAddActivity.class).create().start().resume();
        PasswordAddActivity activity = controller.get();


        Assert.assertEquals("Add Password", activity.getTitle().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        controller.pause().stop().destroy();
    }
}
