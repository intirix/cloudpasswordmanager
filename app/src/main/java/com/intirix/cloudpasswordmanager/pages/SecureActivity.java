package com.intirix.cloudpasswordmanager.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.navigation.NavigationItem;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListNavigationItem;
import com.intirix.cloudpasswordmanager.services.AutoLogoffService;
import com.intirix.cloudpasswordmanager.services.AutoLogoffServiceImpl;
import com.intirix.cloudpasswordmanager.services.SessionService;

import java.util.LinkedList;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by jeff on 7/27/16.
 */
public abstract class SecureActivity extends BaseActivity {

    @Inject
    protected SessionService sessionService;

    @Inject
    protected AutoLogoffService autoLogoffService;

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

    @Override
    protected void addNavigationItems(LinkedList<NavigationItem> navItems) {
        super.addNavigationItems(navItems);
        navItems.addLast(new PasswordListNavigationItem(this));

    }

    protected void logoff() {
        sessionService.end();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
