package com.intirix.cloudpasswordmanager.pages.passworddetail;

import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;
import com.intirix.cloudpasswordmanager.services.ClipboardService;
import com.intirix.cloudpasswordmanager.services.beans.PasswordBean;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class PasswordDetailActivity extends SecureActivity {

    public static String KEY_PASSWORD_INDEX = "PasswordIndex";


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

    @Inject
    ClipboardService clipboardService;

    @Override
    protected int getLayoutId() {
        return R.layout.password_detail;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                updateForm();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password_detail_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.password_detail_password_hide)
    public void onClickHide(View view) {
        password.setText("********{"+passwordBean.getPass().length()+'}');
        passwordShowAction.setVisibility(View.VISIBLE);
        passwordHideAction.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.password_detail_password_show)
    public void onClickShow(View view) {
        password.setText(passwordBean.getPass());
        passwordShowAction.setVisibility(View.INVISIBLE);
        passwordHideAction.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.password_detail_password_copy)
    public void onClickCopyPassword(View v) {
        clipboardService.copyStringToClipboard(getString(R.string.password_detail_password_label), passwordBean.getPass());
    }

    @OnLongClick(R.id.password_detail_password_value)
    public boolean onLongClickPassword(View v) {
        clipboardService.copyStringToClipboard(getString(R.string.password_detail_password_label), passwordBean.getPass());
        return true;
    }

    private void updateForm() {
        website.setText(passwordBean.getWebsite());
        username.setText(passwordBean.getLoginName());
        password.setText("********{"+passwordBean.getPass().length()+'}');
        category.setText(passwordBean.getCategoryName());
        notes.setText(passwordBean.getNotes());

        passwordContainsLower.setEnabled(passwordBean.isHasLower());
        passwordContainsUpper.setEnabled(passwordBean.isHasUpper());
        passwordContainsSpecial.setEnabled(passwordBean.isHasSpecial());
        passwordContainsNumber.setEnabled(passwordBean.isHasNumber());

        if (category.getBackground()!=null && category.getBackground() instanceof PaintDrawable) {
            PaintDrawable bg = (PaintDrawable)category.getBackground();
            bg.getPaint().setColor(passwordBean.getCategoryBackground());
        } else {
            PaintDrawable bg = new PaintDrawable(passwordBean.getCategoryBackground());
            bg.setCornerRadius(16);
            category.setBackgroundDrawable(bg);
        }
    }

}
