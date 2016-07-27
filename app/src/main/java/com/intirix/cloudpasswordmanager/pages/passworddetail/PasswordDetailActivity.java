package com.intirix.cloudpasswordmanager.pages.passworddetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.LoginActivity;
import com.intirix.cloudpasswordmanager.services.SessionService;
import com.intirix.cloudpasswordmanager.services.beans.PasswordBean;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasswordDetailActivity extends AppCompatActivity {

    public static String KEY_PASSWORD_INDEX = "PasswordIndex";


    @Inject
    SessionService sessionService;

    @BindView(R.id.password_detail_website)
    TextView website;

    @BindView(R.id.password_detail_loginName_value)
    TextView username;

    @BindView(R.id.password_detail_password_value)
    TextView password;

    @BindView(R.id.password_detail_password_copy)
    TextView passwordCopyAction;

    @BindView(R.id.password_detail_password_show)
    TextView passwordShowAction;

    @BindView(R.id.password_detail_password_hide)
    TextView passwordHideAction;

    @BindView(R.id.password_detail_contains_capital)
    TextView passwordContainsUpper;

    @BindView(R.id.password_detail_contains_lower)
    TextView passwordContainsLower;

    @BindView(R.id.password_detail_contains_symbol)
    TextView passwordContainsSpecial;

    @BindView(R.id.password_detail_contains_number)
    TextView passwordContainsNumber;

    @BindView(R.id.password_detail_category_value)
    TextView category;

    @BindView(R.id.password_detail_notes_value)
    TextView notes;

    PasswordBean passwordBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ButterKnife.bind(this);
        PasswordApplication.getSInjector(this).inject(this);

        int passwordIndex = getIntent().getIntExtra(KEY_PASSWORD_INDEX, 0);

        if (sessionService.getCurrentSession()==null) {
            logoff();
        } else {
            List<PasswordBean> passwordBeanList = sessionService.getCurrentSession().getPasswordBeanList();

            if (passwordBeanList==null || passwordBeanList.size()<passwordIndex) {
                logoff();
            } else {
                passwordBean = passwordBeanList.get(passwordIndex);
            }
        }

    }

    protected void logoff() {
        sessionService.end();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
