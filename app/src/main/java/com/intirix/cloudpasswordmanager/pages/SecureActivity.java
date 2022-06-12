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
package com.intirix.cloudpasswordmanager.pages;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;
import com.intirix.cloudpasswordmanager.pages.passwordadd.PasswordAddedEvent;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordsLoadedEvent;
import com.intirix.cloudpasswordmanager.services.session.AutoLogoffServiceImpl;
import com.intirix.cloudpasswordmanager.services.settings.OfflineModeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by jeff on 7/27/16.
 */
public abstract class SecureActivity extends BaseActivity {

    private static final String TAG = SecureActivity.class.getSimpleName();

    private Handler handler;

    @Inject
    OfflineModeService offlineModeService;

    private Runnable autoLogoffChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (!autoLogoffService.isSessionStillValid()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    } else {
                        logoff();
                    }
                }
            } finally {
                handler.postDelayed(autoLogoffChecker, AutoLogoffServiceImpl.TIMEOUT / 4);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!BuildConfig.ALLOW_SCREENSHOTS) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        super.onCreate(savedInstanceState);


        ButterKnife.bind(this);
        PasswordApplication.getSInjector(this).inject(this);

        handler = new Handler();
        autoLogoffChecker.run();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(autoLogoffChecker);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuitem_logout:
                logoff();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        autoLogoffService.notifyUserEvent();
        if (!autoLogoffService.isSessionStillValid()) {
            logoff();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        autoLogoffService.notifyUserEvent();
        if (!autoLogoffService.isSessionStillValid()) {
            logoff();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFatalError(FatalErrorEvent event) {
        logoff(event.getMessage());
    }

    protected void logoff() {
        logoff(null);
    }

    protected void logoff(String errorMessage) {
        Log.d(TAG, "logoff()");
        sessionService.end();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        if (errorMessage!=null) {
            intent.putExtra(LoginActivity.PARAM_ERROR_MESSAGE, errorMessage);
        }
        startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordsLoaded(PasswordsLoadedEvent event) {
        offlineModeService.updateOfflineModeCache(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordAdded(PasswordAddedEvent event) {
        offlineModeService.updateOfflineModeCache(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordUpdated(PasswordUpdatedEvent event) {
        offlineModeService.updateOfflineModeCache(false);
    }
}
