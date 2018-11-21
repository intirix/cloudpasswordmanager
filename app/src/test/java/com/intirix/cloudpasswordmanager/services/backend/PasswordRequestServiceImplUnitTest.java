package com.intirix.cloudpasswordmanager.services.backend;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by jeff on 7/15/17.
 */

@RunWith(RobolectricTestRunner.class)


public class PasswordRequestServiceImplUnitTest {

    private PasswordRequestServiceImpl impl;

    private MockSessionService sessionService;

    @Before
    public void setUp() {
        sessionService = new MockSessionService();
        impl = new PasswordRequestServiceImpl(sessionService,null,null);
    }

    @Test
    public void verifyIsRunningWorksWhenSessionIsNull() {
        Assert.assertFalse(impl.isLoginRunning());
    }
}
