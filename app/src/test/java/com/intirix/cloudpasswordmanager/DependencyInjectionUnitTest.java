package com.intirix.cloudpasswordmanager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class DependencyInjectionUnitTest extends BaseTestCase {

    @Test
    public void verifyDependencyInjection() throws Exception {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();

        Assert.assertNotNull(activity.passwordRequestService);
        Assert.assertNotNull(activity.session);

        controller.pause().stop().destroy();
    }
}