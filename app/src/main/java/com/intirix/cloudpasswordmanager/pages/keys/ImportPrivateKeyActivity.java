package com.intirix.cloudpasswordmanager.pages.keys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.BaseActivity;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jeff on 7/28/17.
 */

public class ImportPrivateKeyActivity extends BaseActivity {
    KeyStorageService keyStorageService;

    @BindView(R.id.import_key_body)
    EditText key;

    @Override
    protected int getLayoutId() {
        return R.layout.import_key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PasswordApplication.getSInjector(this).inject(this);
    }

    @OnClick(R.id.import_key_submit)
    public void onImportKey(View view) {
        try {
            keyStorageService.saveEncryptedPrivateKey(key.getText().toString());
        } catch (IOException e) {
            // ignore
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
