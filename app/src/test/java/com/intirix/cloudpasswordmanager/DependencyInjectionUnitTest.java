package com.intirix.cloudpasswordmanager;

import com.intirix.cloudpasswordmanager.injection.MockCloudPasswordManagerModule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class DependencyInjectionUnitTest {

    @Before
    public void setup() {
        TestPasswordApplication app = (TestPasswordApplication) RuntimeEnvironment.application;
        // Setting up the mock module
        MockCloudPasswordManagerModule module = new MockCloudPasswordManagerModule();
        app.setModule(module);
        app.initObjects();
    }

    @Test
    public void verifyDependencyInjection() throws Exception {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();

        Assert.assertNotNull(activity.passwordStorage);
        Assert.assertNotNull(activity.session);

        controller.pause().stop().destroy();
    }
}