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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;
import com.intirix.cloudpasswordmanager.services.AutoLogoffServiceImpl;

import butterknife.ButterKnife;

/**
 * Created by jeff on 7/27/16.
 */
public abstract class SecureActivity extends BaseActivity {

    private Handler handler;

    private Runnable autoLogoffChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (!autoLogoffService.isSessionStillValid()) {
                    logoff();
                }
            } finally {
                handler.postDelayed(autoLogoffChecker, AutoLogoffServiceImpl.TIMEOUT / 4);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);


        ButterKnife.bind(this);
        PasswordApplication.getSInjector(this).inject(this);

        handler = new Handler();
        autoLogoffChecker.run();

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

    protected void logoff() {
        sessionService.end();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
