package com.intirix.cloudpasswordmanager;

import android.support.annotation.NonNull;

import com.intirix.cloudpasswordmanager.injection.CloudPasswordManagerModule;
import com.intirix.cloudpasswordmanager.injection.MockCloudPasswordManagerModule;
import com.intirix.cloudpasswordmanager.injection.ServiceRef;

import org.junit.Before;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by jeff on 6/19/16.
 */
public class BaseTestCase {

    protected TestPasswordApplication app;

    protected ServiceRef serviceRef;

    @Before
    public void setup() {
        app = (TestPasswordApplication) RuntimeEnvironment.application;
        // Setting up the mock module
        CloudPasswordManagerModule module = createMockModule();
        app.setModule(module);
        app.initObjects();

        serviceRef = app.getInjector();
    }

    @NonNull
    protected CloudPasswordManagerModule createMockModule() {
        return new MockCloudPasswordManagerModule(RuntimeEnvironment.application);
    }

}
