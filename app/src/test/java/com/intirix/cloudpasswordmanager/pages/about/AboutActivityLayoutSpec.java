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
package com.intirix.cloudpasswordmanager.pages.about;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.about.AboutActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.android.controller.ActivityController;

import java.text.SimpleDateFormat;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricTestRunner.class)


public class AboutActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyFormElementsExist() throws Exception {
        ActivityController<AboutActivity> controller = Robolectric.buildActivity(AboutActivity.class).create().start().resume();
        AboutActivity activity = controller.get();

        Assert.assertNotNull(activity.version);
        Assert.assertEquals(BuildConfig.VERSION_NAME, activity.version.getText().toString());

        Assert.assertNotNull(activity.gitHash);
        Assert.assertEquals(7, activity.gitHash.getText().length());

        Assert.assertNotNull(activity.buildType);
        Assert.assertEquals(BuildConfig.BUILD_TYPE, activity.buildType.getText().toString());

        Assert.assertNotNull(activity.buildTime);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss zzz");
        Assert.assertEquals(sdf.format(BuildConfig.BUILD_TIME), activity.buildTime.getText().toString());

        controller.pause().stop().destroy();
    }
}
