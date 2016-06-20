package com.intirix.cloudpasswordmanager;

import android.view.View;
import android.widget.Button;

import com.intirix.cloudpasswordmanager.services.MockPasswordStorageService;

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
public class LoginActivityActionSpec extends BaseTestCase {

    @Test
    public void verifyFailedLogin() throws Exception {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";
        final String MOCK_ERROR = "myerror";

        activity.urlInput.setText(MOCK_URL);
        activity.userInput.setText(MOCK_USER);
        activity.passInput.setText(MOCK_PASS);
        Button button = (Button)activity.findViewById(R.id.login_login_button);
        button.performClick();

        // then
        // The form elements are saved in the session
        Assert.assertEquals(MOCK_URL, activity.session.getUrl());
        Assert.assertEquals(MOCK_USER, activity.session.getUsername());
        Assert.assertEquals(MOCK_PASS, activity.session.getPassword());

        // notify the activity of the error
        MockPasswordStorageService service = (MockPasswordStorageService)activity.passwordStorage;
        service.getLastVersionCallback().onError(MOCK_ERROR);
        Assert.assertEquals(MOCK_ERROR, activity.errorMessageView.getText().toString());
        // verify that the error message is visible
        Assert.assertEquals(View.VISIBLE, activity.errorMessageView.getVisibility());


        controller.pause().stop().destroy();
    }
}
