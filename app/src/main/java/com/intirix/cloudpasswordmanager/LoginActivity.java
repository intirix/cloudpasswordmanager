package com.intirix.cloudpasswordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.intirix.cloudpasswordmanager.injection.*;
import com.intirix.cloudpasswordmanager.services.PasswordStorageService;
import com.intirix.cloudpasswordmanager.services.SessionService;
import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Inject
    SessionService session;

    @Inject
    PasswordStorageService passwordStorage;

    @BindView(R.id.login_url)
    EditText urlInput;

    @BindView(R.id.login_user)
    EditText userInput;

    @BindView(R.id.login_pass)
    EditText passInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        PasswordApplication.getSInjector(this).inject(this);
//        ServiceRef sref = com.intirix.cloudpasswordmanager.injection.DaggerServiceRef.builder().cloudPasswordManagerModule(new CloudPasswordManagerModule()).build();

  //      sref.inject(this);
    }

    @OnClick(R.id.login_login_button)
    public void onLogin(View view) {

        Log.d(LoginActivity.class.getSimpleName(),"onLogin()");
        session.setUrl(urlInput.getText().toString());
        session.setUsername(userInput.getText().toString());
        session.setPassword(passInput.getText().toString());
        passwordStorage.getServerVersion(new VersionCallback() {
            @Override
            public void onReturn(String version) {
                Toast.makeText(LoginActivity.this, version, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
