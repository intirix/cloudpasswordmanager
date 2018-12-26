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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.BaseFragment;
import com.intirix.cloudpasswordmanager.services.BiometricService;
import com.intirix.cloudpasswordmanager.services.settings.OfflineModeService;
import com.intirix.cloudpasswordmanager.services.settings.SavePasswordEnum;
import com.intirix.cloudpasswordmanager.services.settings.SavePasswordService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by jeff on 10/6/16.
 */
public class SettingsFragment extends BaseFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Inject
    SavePasswordService savePasswordService;

    @Inject
    OfflineModeService offlineModeService;

    @Inject
    BiometricService biometricService;

    @BindView(R.id.settings_savepass_value)
    TextView currentSavePasswordOptionLabel;

    @BindView(R.id.settings_offline_checkbox)
    CheckBox offlineCheckBox;

    @BindView(R.id.settings_biometric_row)
    View biometricRow;

    @BindView(R.id.settings_biometric_checkbox)
    CheckBox biometricCheckBox;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        SavePasswordOption currentOption = SavePasswordOptionFactory.createOption(savePasswordService.getCurrentSetting(), getActivity(), savePasswordService);
        if (currentOption==null) {
            Log.w(TAG, "Unknown save password policy selected, changing to option NEVER");
            currentOption = SavePasswordOptionFactory.createOption(SavePasswordEnum.NEVER, getActivity(), savePasswordService);
            savePasswordService.changeSavePasswordSetting(SavePasswordEnum.NEVER);
        }
        currentSavePasswordOptionLabel.setText(currentOption.getLabel());
        offlineCheckBox.setChecked(offlineModeService.isOfflineModelEnabled());
        if (biometricService.isBiometricPromptEnabled()) {
            biometricRow.setVisibility(View.VISIBLE);
            biometricCheckBox.setChecked(biometricService.isEnrolled());
        } else {
            biometricRow.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.settings_savepass_row)
    public void onClickChangeSavePassword(View view) {
        baseActivity.navigateRightPane(new SettingsSavePasswordOptionsFragment());
    }

    @OnCheckedChanged(R.id.settings_offline_checkbox)
    public void onOfflineModeClick(CompoundButton button, boolean checked) {
        if (checked) {
            offlineModeService.enable();
            offlineModeService.updateOfflineModeCache(false);
        } else {
            offlineModeService.disable();
        }
    }

    @OnCheckedChanged(R.id.settings_biometric_checkbox)
    public void onBiometricClick(CompoundButton button, boolean checked) {
        if (checked) {
            biometricService.enroll();
        } else {
            biometricService.unenroll();
        }
    }
}
