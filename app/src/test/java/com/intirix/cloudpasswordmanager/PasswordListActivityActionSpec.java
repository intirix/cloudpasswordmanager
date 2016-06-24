package com.intirix.cloudpasswordmanager;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class PasswordListActivityActionSpec extends BaseTestCase {

    @Test
    public void verifyLogoffButton() throws Exception {
        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        activity.session.setUrl(MOCK_URL);
        activity.session.setUsername(MOCK_USER);
        activity.session.setPassword(MOCK_PASS);

        ShadowActivity sact = Shadows.shadowOf(activity);
        sact.onCreateOptionsMenu(new RoboMenu(activity));
        Shadows.shadowOf(activity.findViewById(R.id.my_toolbar)).dump();
        sact.clickMenuItem(R.id.menuitem_logout);


        // verify that the password was cleared out
        Assert.assertNull(activity.session.getPassword());

        // verify that we are starting the PasswordListActivity
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull(intent);
        Assert.assertEquals(LoginActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Assert.assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);


        controller.pause().stop().destroy();
    }

}
