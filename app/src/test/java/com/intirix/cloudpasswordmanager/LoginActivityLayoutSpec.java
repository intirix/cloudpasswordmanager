package com.intirix.cloudpasswordmanager;

import android.view.View;

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
public class LoginActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyFormElementsExist() throws Exception {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();

        Assert.assertNotNull(activity.urlInput);
        Assert.assertNotNull(activity.userInput);
        Assert.assertNotNull(activity.passInput);
        Assert.assertNotNull(activity.errorMessageView);
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());
        Assert.assertNotNull(activity.findViewById(R.id.login_login_button));

        controller.pause().stop().destroy();
    }
}
