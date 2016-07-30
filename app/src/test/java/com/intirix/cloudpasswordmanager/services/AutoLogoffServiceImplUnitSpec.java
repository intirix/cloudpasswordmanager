package com.intirix.cloudpasswordmanager.services;

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
        application = TestPasswordApplication.class)
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
