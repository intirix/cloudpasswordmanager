package com.intirix.cloudpasswordmanager;

import android.support.annotation.NonNull;

import com.intirix.cloudpasswordmanager.injection.CloudPasswordManagerModule;

/**
 * Created by jeff on 6/19/16.
 */
public class TestPasswordApplication extends PasswordApplication {

    CloudPasswordManagerModule module;

    public void setModule(CloudPasswordManagerModule module) {
        this.module = module;
    }

    @NonNull
    @Override
    protected CloudPasswordManagerModule getModule() {
        if (module!=null) {
            return module;
        }
        return super.getModule();
    }
}
