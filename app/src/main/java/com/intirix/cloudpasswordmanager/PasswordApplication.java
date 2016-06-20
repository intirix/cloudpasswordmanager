package com.intirix.cloudpasswordmanager;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.intirix.cloudpasswordmanager.injection.CloudPasswordManagerModule;
import com.intirix.cloudpasswordmanager.injection.ServiceRef;

/**
 * Created by jeff on 6/19/16.
 */
public class PasswordApplication extends Application {

    private ServiceRef serviceRef;

    @Override
    public void onCreate() {
        super.onCreate();
        initObjects();
    }

    /**
     * Initialize the objects
     */
    protected void initObjects() {
        serviceRef = com.intirix.cloudpasswordmanager.injection.DaggerServiceRef.builder().cloudPasswordManagerModule(getModule()).build();
    }

    @NonNull
    protected CloudPasswordManagerModule getModule() {
        return new CloudPasswordManagerModule();
    }

    /**
     * Get the injector
     * @return
     */
    public ServiceRef getInjector() {
        return serviceRef;
    }

    /**
     * Get the injector
     * @param ctx
     * @return
     */
    public static ServiceRef getSInjector(Context ctx) {
        PasswordApplication app = (PasswordApplication)ctx.getApplicationContext();
        return app.getInjector();
    }
}
