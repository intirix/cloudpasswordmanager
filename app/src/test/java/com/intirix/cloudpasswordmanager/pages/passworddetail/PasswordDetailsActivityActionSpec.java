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
package com.intirix.cloudpasswordmanager.pages.passworddetail;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
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
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowClipboardManager;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;
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
        PasswordBean bean = startValidPasswordSession();

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
    public void verifyCopyButtonCopiesPassword() {
        PasswordBean bean = startValidPasswordSession();

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        activity.passwordCopyAction.performClick();

        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ShadowClipboardManager shadowClipboardManager = Shadows.shadowOf(clipboard);
        ClipData primaryClip = shadowClipboardManager.getPrimaryClip();
        Assert.assertNotNull("Missing expected clipboard paste", primaryClip);
        Assert.assertEquals(1, primaryClip.getItemCount());
        Assert.assertEquals(bean.getPass(), primaryClip.getItemAt(0).getText().toString());

        ShadowLooper.idleMainLooper();
        Assert.assertEquals(activity.getString(R.string.password_detail_password_toast), ShadowToast.getTextOfLatestToast());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyLongPressPasswordCopiesPassword() {
        PasswordBean bean = startValidPasswordSession();

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        activity.password.performLongClick();

        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ShadowClipboardManager shadowClipboardManager = Shadows.shadowOf(clipboard);
        ClipData primaryClip = shadowClipboardManager.getPrimaryClip();
        Assert.assertNotNull("Missing expected clipboard paste", primaryClip);
        Assert.assertEquals(1, primaryClip.getItemCount());
        Assert.assertEquals(bean.getPass(), primaryClip.getItemAt(0).getText().toString());

        ShadowLooper.idleMainLooper();
        Assert.assertEquals(activity.getString(R.string.password_detail_password_toast), ShadowToast.getTextOfLatestToast());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyLongPressUsernameCopiesUsername() {
        PasswordBean bean = startValidPasswordSession();

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        activity.username.performLongClick();

        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ShadowClipboardManager shadowClipboardManager = Shadows.shadowOf(clipboard);
        ClipData primaryClip = shadowClipboardManager.getPrimaryClip();
        Assert.assertNotNull("Missing expected clipboard paste", primaryClip);
        Assert.assertEquals(1, primaryClip.getItemCount());
        Assert.assertEquals(bean.getLoginName(), primaryClip.getItemAt(0).getText().toString());

        ShadowLooper.idleMainLooper();
        Assert.assertEquals(activity.getString(R.string.password_detail_loginName_toast), ShadowToast.getTextOfLatestToast());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyLongPressWebsiteCopiesUrl() {
        PasswordBean bean = startValidPasswordSession();
        bean.setAddress("http://www.github.com/login");

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        activity.website.performLongClick();

        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ShadowClipboardManager shadowClipboardManager = Shadows.shadowOf(clipboard);
        ClipData primaryClip = shadowClipboardManager.getPrimaryClip();
        Assert.assertNotNull("Missing expected clipboard paste", primaryClip);
        Assert.assertEquals(1, primaryClip.getItemCount());
        Assert.assertEquals(bean.getAddress(), primaryClip.getItemAt(0).getText().toString());

        ShadowLooper.idleMainLooper();
        Assert.assertEquals(activity.getString(R.string.password_detail_website_toast), ShadowToast.getTextOfLatestToast());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyLongPressWebsiteCopiesWebsiteWhenThereIsNoUrl() {
        PasswordBean bean = startValidPasswordSession();

        Intent intent = new Intent();
        intent.putExtra(PasswordDetailActivity.KEY_PASSWORD_INDEX, 0);

        ActivityController<PasswordDetailActivity> controller = Robolectric.buildActivity(PasswordDetailActivity.class).withIntent(intent).create().start().resume();
        PasswordDetailActivity activity = controller.get();

        activity.website.performLongClick();

        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ShadowClipboardManager shadowClipboardManager = Shadows.shadowOf(clipboard);
        ClipData primaryClip = shadowClipboardManager.getPrimaryClip();
        Assert.assertNotNull("Missing expected clipboard paste", primaryClip);
        Assert.assertEquals(1, primaryClip.getItemCount());
        Assert.assertEquals(bean.getWebsite(), primaryClip.getItemAt(0).getText().toString());

        ShadowLooper.idleMainLooper();
        Assert.assertEquals(activity.getString(R.string.password_detail_website_toast), ShadowToast.getTextOfLatestToast());

        controller.pause().stop().destroy();
    }

    @NonNull
    protected PasswordBean startValidPasswordSession() {
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
        return bean;
    }

    @Test
    public void verifyShowPasswordResetsOnRotate() {
        PasswordBean bean = startValidPasswordSession();

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

    @Test
    public void verifyLogoffButton() throws Exception {
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

        ShadowActivity sact = Shadows.shadowOf(activity);
        sact.onCreateOptionsMenu(new RoboMenu(activity));
        Shadows.shadowOf(activity.findViewById(R.id.my_toolbar)).dump();
        sact.clickMenuItem(R.id.menuitem_logout);


        // verify that the sessionService was cleared out
        Assert.assertNull(sessionService.getCurrentSession());
        assertLogOff(activity);


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
