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
package com.intirix.cloudpasswordmanager.pages.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.BaseFragment;
import com.intirix.cloudpasswordmanager.services.SavePasswordEnum;
import com.intirix.cloudpasswordmanager.services.SavePasswordService;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jeff on 10/6/16.
 */
public class SettingsFragment extends BaseFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Inject
    SavePasswordService savePasswordService;

    @BindView(R.id.settings_savepass_value)
    TextView currentSavePasswordOptionLabel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PasswordApplication.getSInjector(getActivity()).inject(this);
        ButterKnife.bind(this, getActivity());

        SavePasswordOption currentOption = SavePasswordOptionFactory.createOption(savePasswordService.getCurrentSetting(), getActivity(), savePasswordService);
        if (currentOption==null) {
            Log.w(TAG, "Unknown save password policy selected, changing to option NEVER");
            currentOption = SavePasswordOptionFactory.createOption(SavePasswordEnum.NEVER, getActivity(), savePasswordService);
            savePasswordService.changeSavePasswordSetting(SavePasswordEnum.NEVER);
        }
        currentSavePasswordOptionLabel.setText(currentOption.getLabel());

    }

    @OnClick(R.id.settings_savepass_row)
    public void onClickChangeSavePassword(View view) {
        baseActivity.navigateRightPane(new SettingsSavePasswordOptionsFragment());
    }
}
