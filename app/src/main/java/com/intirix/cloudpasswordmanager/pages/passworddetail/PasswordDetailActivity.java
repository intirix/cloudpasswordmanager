package com.intirix.cloudpasswordmanager.pages.passworddetail;

import android.app.Activity;
import android.os.Bundle;

import com.intirix.cloudpasswordmanager.R;

public class PasswordDetailActivity extends Activity {

    public static String KEY_PASSWORD_INDEX = "PasswordIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_detail);
    }
}
