package com.intirix.cloudpasswordmanager.pages.passwordadd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.ErrorEvent;
import com.intirix.cloudpasswordmanager.pages.InfoEvent;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class PasswordAddActivity extends SecureActivity {

    private static final String TAG = PasswordAddActivity.class.getSimpleName();

    @BindView(R.id.password_add_website)
    EditText websiteInput;

    @BindView(R.id.password_add_username)
    EditText usernameInput;

    @BindView(R.id.password_add_password)
    EditText passwordInput;

    @BindView(R.id.password_add_error_message)
    TextView errorMessageView;

    @BindView(R.id.password_add_add_button)
    View addButton;

    ProgressDialog progressDialog;

    @Inject
    PasswordRequestService passwordRequestService;

    @Override
    protected int getLayoutId() {
        return R.layout.password_add;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PasswordApplication.getSInjector(this).inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateErrorMessageVisibility();
        updateProgressDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password_add_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateErrorMessageVisibility() {
        if (errorMessageView.getText().length() > 0) {
            errorMessageView.setVisibility(View.VISIBLE);
        } else {
            errorMessageView.setVisibility(View.GONE);
        }
    }

    /**
     * Update the state of the progress spinner based on the state of the crud request
     */
    private void updateProgressDialog() {
        updateProgressDialog(null);
    }

    /**
     * Update the state of the progress spinner with a new message
     * @param message
     */
    private void updateProgressDialog(String message) {
        String msg = message;
        if (msg==null){
            msg = getString(R.string.password_add_progress_message);
        }
        if (passwordRequestService.isCrudRunning()) {
            // if the progress dialog doesn't exist, then create it
            if (progressDialog == null) {
                Log.d(TAG, "Crud request running, creating waiting dialog");
                progressDialog = ProgressDialog.show(this, "", msg);
            } else {
                Log.d(TAG, "Crud request running, updating waiting dialog");
                progressDialog.setMessage(msg);
            }
            // show the dialog
            progressDialog.show();
        } else if (progressDialog!=null) {
            // if the request is not running, then we only need to
            // dismiss the dialog if it was previously created
            progressDialog.dismiss();
        }

    }


    @OnClick(R.id.password_add_add_button)
    public void onAddClicked(View view) {
        try {

            if (websiteInput.getText().length() == 0) {
                errorMessageView.setText(R.string.error_empty_url);
                updateErrorMessageVisibility();
            } else {
                PasswordBean bean = new PasswordBean();
                bean.setAddress(websiteInput.getText().toString());
                bean.setWebsite(websiteInput.getText().toString());
                bean.setNotes("");
                bean.setDateChanged(Calendar.getInstance());
                bean.setLoginName(usernameInput.getText().toString());
                bean.setPass(passwordInput.getText().toString());

                passwordRequestService.addPassword(bean);
                updateProgressDialog();
            }
        } catch (Exception e) {
            errorMessageView.setText(e.getMessage());
            updateErrorMessageVisibility();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordAdded(PasswordAddedEvent event) {
        Log.d(TAG,"onPasswordAdded()");
        updateProgressDialog();

        passwordRequestService.listPasswords();
        passwordRequestService.listCategories();

        Intent intent = new Intent(PasswordAddActivity.this, PasswordListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onError(ErrorEvent event) {
        errorMessageView.setText(event.getMessage());
        updateErrorMessageVisibility();
        updateProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInfo(InfoEvent event) {
        updateProgressDialog(event.getMessage());
    }
}
