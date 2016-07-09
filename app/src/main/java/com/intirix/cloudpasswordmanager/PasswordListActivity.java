package com.intirix.cloudpasswordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.intirix.cloudpasswordmanager.services.SessionService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasswordListActivity extends AppCompatActivity {

    @Inject
    SessionService session;

    @BindView(R.id.password_list_recycler)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ButterKnife.bind(this);
        PasswordApplication.getSInjector(this).inject(this);

        // if the session has ended, then send us back to the logon page
        if (session.getCurrentSession()==null) {
            logoff();
        }

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

    protected void logoff() {
        session.end();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
