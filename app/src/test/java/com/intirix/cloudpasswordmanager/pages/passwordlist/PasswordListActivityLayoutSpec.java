package com.intirix.cloudpasswordmanager.pages.passwordlist;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.SessionService;
import com.intirix.cloudpasswordmanager.services.beans.PasswordInfo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class PasswordListActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyBaseLayout() throws Exception {
        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertNotNull(activity.recyclerView);

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyRowLayout() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        PasswordInfo pass1 = new PasswordInfo();
        pass1.setWebsite("www.gmail.com");
        List<PasswordInfo> passwords = new ArrayList<>();
        passwords.add(pass1);
        sessionService.getCurrentSession().setPasswordList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        PasswordListViewHolder vh = activity.adapter.onCreateViewHolder(activity.recyclerView, 0);
        activity.adapter.onBindViewHolder(vh, 0);
        Assert.assertEquals(pass1.getWebsite(), vh.website.getText().toString());

        controller.pause().stop().destroy();

    }
}
