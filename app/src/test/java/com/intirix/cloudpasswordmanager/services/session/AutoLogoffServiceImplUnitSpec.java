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
package com.intirix.cloudpasswordmanager.services.session;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by jeff on 7/29/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class AutoLogoffServiceImplUnitSpec {

    private MockSessionService sessionService;

    private AutoLogoffServiceImplTimeShift impl;

    @Before
    public void setUp() {
        sessionService = new MockSessionService();

        impl = new AutoLogoffServiceImplTimeShift(sessionService);
    }

    @Test
    public void verifyNotAutoLogoffWhenSessionIsValid() {
        sessionService.start();
        Assert.assertTrue(impl.isSessionStillValid());
    }

    @Test
    public void verifyAutoLogoffWhenSessionIsNull() {
        Assert.assertFalse(impl.isSessionStillValid());
    }

    @Test
    public void verifyNotAutoLogoffBeforeTimeout() {
        sessionService.start();
        Assert.assertTrue(impl.isSessionStillValid());
        impl.addTimeShift(AutoLogoffServiceImpl.TIMEOUT/2);
        Assert.assertTrue(impl.isSessionStillValid());
    }

    @Test
    public void verifyAutoLogoffAfterTimeout() {
        sessionService.start();
        Assert.assertTrue(impl.isSessionStillValid());
        impl.addTimeShift(AutoLogoffServiceImpl.TIMEOUT+2);
        Assert.assertFalse(impl.isSessionStillValid());
    }

    @Test
    public void verifyUserEventPreventsAutoLogoff() {
        sessionService.start();
        long start = impl.getCurrentTime();
        Assert.assertTrue(impl.isSessionStillValid());

        for (int i=0; i < 10; i++) {
            impl.addTimeShift(AutoLogoffServiceImpl.TIMEOUT/2);
            impl.notifyUserEvent();
            Assert.assertTrue(impl.isSessionStillValid());

        }
        long end = impl.getCurrentTime();
        long dt = end - start;
        Assert.assertTrue(dt> AutoLogoffServiceImpl.TIMEOUT);

    }

    @Test
    public void verifyUserEventAfterTimeoutDoesNotResetTimeout() {
        sessionService.start();
        long start = impl.getCurrentTime();
        Assert.assertTrue(impl.isSessionStillValid());
        impl.addTimeShift(AutoLogoffServiceImpl.TIMEOUT+2);
        impl.notifyUserEvent();
        Assert.assertFalse(impl.isSessionStillValid());

        long end = impl.getCurrentTime();
        long dt = end - start;
        Assert.assertTrue(dt> AutoLogoffServiceImpl.TIMEOUT);
    }

}
