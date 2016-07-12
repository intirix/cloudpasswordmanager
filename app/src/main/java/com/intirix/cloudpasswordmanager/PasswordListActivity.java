package com.intirix.cloudpasswordmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.intirix.cloudpasswordmanager.events.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.events.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.SessionService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasswordListActivity extends AppCompatActivity {

    @Inject
    SessionService sessionService;

    @BindView(R.id.password_list_recycler)
    RecyclerView recyclerView;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ButterKnife.bind(this);
        PasswordApplication.getSInjector(this).inject(this);

        // if the session has ended, then send us back to the logon page
        if (sessionService.getCurrentSession()==null) {
            logoff();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateProgressDialog();
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password_list_action, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordsUpdated(PasswordListUpdatedEvent event) {
        updateProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoriesUpdated(CategoryListUpdatedEvent event) {
        updateProgressDialog();
    }

    protected void logoff() {
        sessionService.end();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateProgressDialog() {
        if (sessionService.getCurrentSession()!=null) {
            boolean showDialog = sessionService.getCurrentSession().getCategoryList()==null||sessionService.getCurrentSession().getPasswordList()==null;

            if (showDialog) {
                if (progressDialog==null) {
                    progressDialog = ProgressDialog.show(this, "", "Loading");
                } else {
                    progressDialog.show();
                }
            } else if (progressDialog!=null) {
                progressDialog.dismiss();
            }
        }
    }
}
