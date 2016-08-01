package com.intirix.cloudpasswordmanager.pages;

import android.widget.TextView;

import com.intirix.cloudpasswordmanager.R;

import butterknife.BindView;

/**
 * Created by jeff on 8/1/16.
 */
public class AboutActivity extends PublicActivity {

    @BindView(R.id.about_version_value)
    TextView version;

    @BindView(R.id.about_githash_value)
    TextView gitHash;

    @BindView(R.id.about_buildtype_value)
    TextView buildType;

    @BindView(R.id.about_buildtime_value)
    TextView buildTime;

    @Override
    protected int getLayoutId() {
        return R.layout.about;
    }
}
