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
package com.intirix.cloudpasswordmanager.pages.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.BaseActivity;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.keys.ImportPrivateKeyActivity;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.session.StorageType;
import com.intirix.cloudpasswordmanager.services.settings.OfflineModeService;
import com.intirix.cloudpasswordmanager.services.settings.SavePasswordService;
import com.intirix.cloudpasswordmanager.services.ssl.CertPinningService;
import com.intirix.cloudpasswordmanager.services.ssl.PinFailedEvent;
import com.intirix.cloudpasswordmanager.services.ssl.PinSuccessfulEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URL;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String PARAM_ERROR_MESSAGE = "errorMessage";

    @Inject
    PasswordRequestService passwordRequestService;

    @Inject
    CertPinningService certPinningService;

    @Inject
    SavePasswordService savePasswordService;

    @Inject
    OfflineModeService offlineModeService;

    @BindView(R.id.login_storage_type)
    Spinner storageTypeSpinner;

    StorageTypeAdapter storageTypeAdapter;

    @BindView(R.id.login_url)
    EditText urlInput;

    @BindView(R.id.login_user)
    EditText userInput;

    @BindView(R.id.login_pass)
    EditText passInput;

    @BindView(R.id.login_error_message)
    TextView errorMessageView;

    @BindView(R.id.login_login_button)
    View loginButton;

    @BindView(R.id.login_pin_button)
    View pinButton;

    @BindView(R.id.login_unpin_button)
    View unpinButton;

    @BindView(R.id.login_import_key_button)
    View importKeyButton;

    ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PasswordApplication.getSInjector(this).inject(this);
        certPinningService.init();
        attachImeGo(passInput);

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
            urlInput.setText(sessionService.getUrl());
        }

        if (userInput.getText().toString().length()==0) {
            userInput.setText(sessionService.getUsername());
        }

        if (savePasswordService.isPasswordAvailable() && passInput.getText().toString().length()==0) {
            passInput.setText(savePasswordService.getPassword());
        }

        // Only set the error message if the current view is empty
        // If another error occurs, we won't want the originally passed in error
        // to override the new error
        Intent intent = getIntent();
        String paramError = intent.getStringExtra(PARAM_ERROR_MESSAGE);
        if (errorMessageView.getText().length()==0 && paramError !=null) {
            errorMessageView.setText(paramError);
        }

        // only show the error message if the view is populated
        updateErrorMessageVisibility();

        if (storageTypeAdapter==null) {
            storageTypeAdapter = new StorageTypeAdapter(this,android.R.layout.simple_spinner_item,
                    StorageType.OWNCLOUD_PASSWORDS, StorageType.SECRETS_MANAGER_API_V1);
            storageTypeSpinner.setAdapter(storageTypeAdapter);

            int selectedPosition = storageTypeAdapter.getPosition(sessionService.getStorageType());
            storageTypeSpinner.setSelection(selectedPosition);
        }

        // recheck the pinning flags
        updateLoginForm(false);

        // when coming back from a rotate, re-show the progress dialog if needed
        updateProgressDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // prevent window leak
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }

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

    void updateLoginForm(boolean clearError) {
        String url = urlInput.getText().toString();
        try {
            if (clearError) {
                // default hiding error messages and enabling everything
                errorMessageView.setText("");
                errorMessageView.setVisibility(View.GONE);
            }
            loginButton.setEnabled(true);
            pinButton.setEnabled(true);
            unpinButton.setEnabled(true);
            urlInput.setEnabled(true);
            importKeyButton.setEnabled(true);

            if (storageTypeSpinner.getSelectedItemPosition()==0) {
                importKeyButton.setEnabled(false);
                importKeyButton.setVisibility(View.GONE);
            } else {
                importKeyButton.setEnabled(true);
                importKeyButton.setVisibility(View.VISIBLE);
            }


            if (url.length()==0) {
                urlInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_highlight_off_black_24dp, 0, 0, 0);
                loginButton.setEnabled(false);
                pinButton.setEnabled(false);
            } else if (certPinningService.isEnabled()) {
                // validate that the url is valid
                new URL(url);

                urlInput.setEnabled(false);
                pinButton.setVisibility(View.GONE);
                pinButton.setEnabled(false);
                unpinButton.setVisibility(View.VISIBLE);
                unpinButton.setEnabled(true);

                if (certPinningService.isValid()) {
                    urlInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_enhanced_encryption_black_24dp, 0, 0, 0);
                } else {
                    urlInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pin, 0, 0, 0);
                }
            } else {
                // validate that the url is valid
                new URL(url);

                if (url.startsWith("https")) {
                    pinButton.setVisibility(View.VISIBLE);
                    pinButton.setEnabled(true);
                    unpinButton.setVisibility(View.GONE);
                    unpinButton.setEnabled(false);

                    urlInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
                } else {
                    pinButton.setVisibility(View.VISIBLE);
                    pinButton.setEnabled(false);
                    unpinButton.setVisibility(View.GONE);
                    unpinButton.setEnabled(false);
                    urlInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_encryption_black_24dp, 0, 0, 0);
                }
            }
        } catch (Exception e) {
            errorMessageView.setText(e.getMessage());
            errorMessageView.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            pinButton.setVisibility(View.VISIBLE);
            pinButton.setEnabled(false);
            unpinButton.setVisibility(View.INVISIBLE);
            unpinButton.setEnabled(false);
            urlInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_highlight_off_black_24dp, 0, 0, 0);
        }
    }

    @OnTextChanged(R.id.login_url)
    public void onUrlChanged(CharSequence charSequence, int a, int b, int c) {
        updateLoginForm(true);
    }

    @OnClick(R.id.login_login_button)
    public void onLogin(View view) {

        try {
            if (urlInput.getText().length() == 0) {
                errorMessageView.setText(R.string.error_empty_url);
                updateErrorMessageVisibility();
            } else if (!urlInput.getText().toString().startsWith("http")) {
                errorMessageView.setText(R.string.error_bad_url);
                updateErrorMessageVisibility();
            } else {

                errorMessageView.setText("");
                updateErrorMessageVisibility();

                Log.d(LoginActivity.class.getSimpleName(), "onLogin() - "+new URL(urlInput.getText().toString()));
                sessionService.setStorageType((StorageType)storageTypeSpinner.getSelectedItem());
                sessionService.setUrl(urlInput.getText().toString());
                sessionService.setUsername(userInput.getText().toString());
                sessionService.start();
                sessionService.getCurrentSession().setPassword(passInput.getText().toString());

                passwordRequestService.login();
                updateProgressDialog();
                if (offlineModeService.isOfflineModelEnabled()) {
                    offlineModeService.loadDataFromCache(false,sessionService.getUsername(), sessionService.getCurrentSession().getPassword());
                }
            }
        } catch (Exception e) {
            errorMessageView.setText(e.getMessage());
            updateErrorMessageVisibility();
        }
    }


    @OnClick(R.id.login_pin_button)
    public void onPin(View view) {
        certPinningService.pin(urlInput.getText().toString());
        errorMessageView.setText("");
        updateErrorMessageVisibility();
        updateProgressDialog();
    }

    @OnClick(R.id.login_unpin_button)
    public void onUnpin(View view) {
        certPinningService.disable();
        errorMessageView.setText("");
        updateErrorMessageVisibility();
        updateLoginForm(true);
    }

    @OnClick(R.id.login_import_key_button)
    public void onImportKey(View view) {
        Intent intent = new Intent(LoginActivity.this, ImportPrivateKeyActivity.class);
        startActivity(intent);

    }

    @OnItemSelected(R.id.login_storage_type)
    public void spinnerItemSelected(Spinner spinner, int position) {
        updateLoginForm(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPinSuccess(PinSuccessfulEvent event) {
        updateProgressDialog();
        updateLoginForm(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPinFailure(PinFailedEvent event) {
        updateProgressDialog();
        errorMessageView.setText(event.getMessage());
        updateErrorMessageVisibility();
        updateLoginForm(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginSuccessfulEvent event) {
        Log.d(TAG,"onLogin()");
        updateProgressDialog();

        passwordRequestService.listPasswords();
        passwordRequestService.listCategories();
        passwordRequestService.listUsers();

        Intent intent = new Intent(LoginActivity.this, PasswordListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFatalError(FatalErrorEvent event) {
        updateProgressDialog();
        sessionService.end();
        errorMessageView.setText(event.getMessage());
        updateErrorMessageVisibility();
    }

    /**
     * Update the state of the progress spinner based on the state of the login request
     */
    private void updateProgressDialog() {
        Log.d(TAG,"updateProgressDialog()");
        if (passwordRequestService.isLoginRunning()) {
            // if the progress dialog doesn't exist, then create it
            if (progressDialog == null) {
                Log.d(TAG, "Login request running, creating login dialog");
                progressDialog = ProgressDialog.show(this, "", getString(R.string.login_progress_message));
            } else {
                Log.d(TAG, "Login request running, updating login dialog");
                progressDialog.setMessage(getString(R.string.login_progress_message));
            }
            // show the dialog
            progressDialog.show();
        } else if (certPinningService.isPinRequestRunning()) {
            // if the progress dialog doesn't exist, then create it
            if (progressDialog == null) {
                Log.d(TAG, "Pin request running, creating pin dialog");
                progressDialog = ProgressDialog.show(this, "", getString(R.string.login_pin_progress_message));
            } else {
                Log.d(TAG, "Pin request running, updating pin dialog");
                progressDialog.setMessage(getString(R.string.login_pin_progress_message));
            }
            // show the dialog
            progressDialog.show();
        } else if (progressDialog!=null) {
            // if the request is not running, then we only need to
            // dismiss the dialog if it was previously created
            progressDialog.dismiss();
        }

    }

    private void attachImeGo(View v) {
        if (v!=null && v instanceof EditText) {
            final EditText et = (EditText)v;
            et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO||actionId == EditorInfo.IME_ACTION_DONE) {
                        onLogin(v);
                    }
                    return true;
                }
            });

            et.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction()==KeyEvent.ACTION_DOWN
                            && (keyCode==KeyEvent.KEYCODE_ENTER||keyCode==KeyEvent.KEYCODE_DPAD_CENTER)) {
                        onLogin(v);
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
