package com.intirix.cloudpasswordmanager.pages.passworddetail;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;

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
public class PasswordDetailsActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyBaseLayout() throws Exception {
        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        Assert.assertNotNull(activity.website);
        Assert.assertNotNull(activity.username);
        Assert.assertNotNull(activity.password);
        Assert.assertNotNull(activity.passwordContainsLower);
        Assert.assertNotNull(activity.passwordContainsNumber);
        Assert.assertNotNull(activity.passwordContainsSpecial);
        Assert.assertNotNull(activity.passwordContainsUpper);
        Assert.assertNotNull(activity.passwordCopyAction);
        Assert.assertNotNull(activity.passwordHideAction);
        Assert.assertNotNull(activity.passwordShowAction);
        Assert.assertNotNull(activity.category);
        Assert.assertNotNull(activity.notes);

        controller.pause().stop().destroy();
    }


}
