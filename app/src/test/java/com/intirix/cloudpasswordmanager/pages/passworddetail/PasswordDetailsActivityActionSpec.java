package com.intirix.cloudpasswordmanager.pages.passworddetail;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.LoginActivity;
import com.intirix.cloudpasswordmanager.services.SessionService;
import com.intirix.cloudpasswordmanager.services.beans.PasswordBean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class PasswordDetailsActivityActionSpec extends BaseTestCase {

    @Test
    public void verifyNoIntentLogsUserOff() throws Exception {
        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        controller.pause().stop().destroy();

        assertLogOff(activity);
    }


    @Test
    public void verifyEmptySessionLogsUserOff() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);
        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        controller.pause().stop().destroy();

        assertLogOff(activity);
    }


    @Test
    public void verifyInvalidIndexLogsUserOff() throws Exception {


        SessionService sessionService = serviceRef.sessionService();

        List<PasswordBean> passwords = new ArrayList<>();

        sessionService.start();
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 1);
        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        controller.pause().stop().destroy();

        assertLogOff(activity);
    }


    @Test
    public void verifyValidPassword() throws Exception {


        SessionService sessionService = serviceRef.sessionService();

        List<PasswordBean> passwords = new ArrayList<>();

        PasswordBean bean = new PasswordBean();
        bean.setPass("12345678");
        passwords.add(bean);

        sessionService.start();
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);
        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        controller.pause().stop().destroy();

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent nextIntent = shadowActivity.peekNextStartedActivity();
        Assert.assertNull("We expected stay on this activity, but are not", nextIntent);
    }


    @Test
    public void verifyHideShowPassword() {
        SessionService sessionService = serviceRef.sessionService();

        List<PasswordBean> passwords = new ArrayList<>();

        PasswordBean bean = new PasswordBean();

        bean.setWebsite("www.facebook.com");
        bean.setLoginName("markz");
        bean.setPass("ABCD!@#$");
        bean.setHasLower(false);
        bean.setHasUpper(true);
        bean.setHasNumber(false);
        bean.setHasSpecial(true);
        bean.setCategoryName("Social");
        bean.setNotes("My facebook login");

        passwords.add(bean);

        sessionService.start();
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();


        Assert.assertEquals("********{"+bean.getPass().length()+'}', activity.password.getText().toString());
        Assert.assertEquals(View.INVISIBLE, activity.passwordHideAction.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.passwordShowAction.getVisibility());

        activity.passwordShowAction.performClick();

        Assert.assertEquals(bean.getPass(), activity.password.getText().toString());
        Assert.assertEquals(View.VISIBLE, activity.passwordHideAction.getVisibility());
        Assert.assertEquals(View.INVISIBLE, activity.passwordShowAction.getVisibility());

        activity.passwordHideAction.performClick();

        Assert.assertEquals("********{"+bean.getPass().length()+'}', activity.password.getText().toString());
        Assert.assertEquals(View.INVISIBLE, activity.passwordHideAction.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.passwordShowAction.getVisibility());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyShowPasswordResetsOnRotate() {
        SessionService sessionService = serviceRef.sessionService();

        List<PasswordBean> passwords = new ArrayList<>();

        PasswordBean bean = new PasswordBean();

        bean.setWebsite("www.facebook.com");
        bean.setLoginName("markz");
        bean.setPass("ABCD!@#$");
        bean.setHasLower(false);
        bean.setHasUpper(true);
        bean.setHasNumber(false);
        bean.setHasSpecial(true);
        bean.setCategoryName("Social");
        bean.setNotes("My facebook login");

        passwords.add(bean);

        sessionService.start();
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();


        Assert.assertEquals("********{"+bean.getPass().length()+'}', activity.password.getText().toString());
        Assert.assertEquals(View.INVISIBLE, activity.passwordHideAction.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.passwordShowAction.getVisibility());

        activity.passwordShowAction.performClick();

        Assert.assertEquals(bean.getPass(), activity.password.getText().toString());
        Assert.assertEquals(View.VISIBLE, activity.passwordHideAction.getVisibility());
        Assert.assertEquals(View.INVISIBLE, activity.passwordShowAction.getVisibility());

        activity.recreate();

        Assert.assertEquals("********{"+bean.getPass().length()+'}', activity.password.getText().toString());
        Assert.assertEquals(View.INVISIBLE, activity.passwordHideAction.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.passwordShowAction.getVisibility());

        controller.pause().stop().destroy();
    }


    protected void assertLogOff(Activity activity) {
        // verify that we are starting the LoginActivity
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull("We expected to change activity, but are not", intent);
        Assert.assertEquals(LoginActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Assert.assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
