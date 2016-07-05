package com.intirix.cloudpasswordmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.events.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.events.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.services.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.SessionService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Inject
    SessionService session;

    @Inject
    PasswordRequestService passwordRequestService;

    @BindView(R.id.login_url)
    EditText urlInput;

    @BindView(R.id.login_user)
    EditText userInput;

    @BindView(R.id.login_pass)
    EditText passInput;

    @BindView(R.id.login_error_message)
    TextView errorMessageView;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ButterKnife.bind(this);
        PasswordApplication.getSInjector(this).inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (urlInput.getText().toString().length()==0) {
            urlInput.setText(session.getUrl());
        }

        if (userInput.getText().toString().length()==0) {
            userInput.setText(session.getUsername());
        }

        // only show the error message if the view is populated
        updateErrorMessageVisibility();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void updateErrorMessageVisibility() {
        if (errorMessageView.getText().length()>0) {
            errorMessageView.setVisibility(View.VISIBLE);
        } else {
            errorMessageView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.login_login_button)
    public void onLogin(View view) {

        Log.d(LoginActivity.class.getSimpleName(),"onLogin()");
        session.setUrl(urlInput.getText().toString());
        session.setUsername(userInput.getText().toString());
        session.start();
        session.getCurrentSession().setPassword(passInput.getText().toString());

        passwordRequestService.login();
        updateProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginSuccessfulEvent event) {
        updateProgressDialog();
        Intent intent = new Intent(LoginActivity.this, PasswordListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFatalError(FatalErrorEvent event) {
        updateProgressDialog();
        session.end();
        errorMessageView.setText(event.getMessage());
        updateErrorMessageVisibility();
    }

    /**
     * Update the state of the progress spinner based on the state of the login request
     */
    private void updateProgressDialog() {
        if (passwordRequestService.isLoginRunning()) {
            // if the progress dialog doesn't exist, then create it
            if (progressDialog==null) {
                progressDialog = ProgressDialog.show(this, "", "Logging in");
            }
            // show the dialog
            progressDialog.show();
        } else if (progressDialog!=null) {
            // if the request is not running, then we only need to
            // dismiss the dialog if it was previously created
            progressDialog.dismiss();
        }

    }
}
