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
package com.intirix.cloudpasswordmanager.pages.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.services.SavePasswordEnum;
import com.intirix.cloudpasswordmanager.services.SavePasswordService;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class LoginActivitySavePasswordSpec extends BaseTestCase {



    @Test
    public void verifyPasswordPopulatedWhenAlwaysSaved() throws Exception {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        // given the user is on the login page
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";
        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        SavePasswordService savePasswordService = activity.savePasswordService;
        savePasswordService.changeSavePasswordSetting(SavePasswordEnum.ALWAYS);
        sessionService.end();

        controller.start().resume();

        Assert.assertEquals(MOCK_PASS, activity.passInput.getText().toString());


        controller.pause().stop().destroy();
    }


}
