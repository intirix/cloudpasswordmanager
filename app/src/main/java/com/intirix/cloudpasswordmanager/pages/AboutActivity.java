package com.intirix.cloudpasswordmanager.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;

import java.text.SimpleDateFormat;

import butterknife.BindView;

/**
 * Created by jeff on 8/1/16.
 */
public class AboutActivity extends BaseActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        version.setText(BuildConfig.VERSION_NAME);
        gitHash.setText(BuildConfig.GIT_HASH);
        buildType.setText(BuildConfig.BUILD_TYPE);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss zzz");
        buildTime.setText(sdf.format(BuildConfig.BUILD_TIME));
    }
}
