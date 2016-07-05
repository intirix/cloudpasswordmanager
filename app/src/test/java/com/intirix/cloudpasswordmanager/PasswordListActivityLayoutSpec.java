package com.intirix.cloudpasswordmanager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class PasswordListActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyFormElementsExist() throws Exception {
        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertNotNull(activity.recyclerView);

        controller.pause().stop().destroy();
    }
}
