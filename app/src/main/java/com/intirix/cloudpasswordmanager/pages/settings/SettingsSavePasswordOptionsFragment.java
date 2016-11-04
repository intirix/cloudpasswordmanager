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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.BaseFragment;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListAdapter;
import com.intirix.cloudpasswordmanager.services.SavePasswordService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeff on 10/17/16.
 */
public class SettingsSavePasswordOptionsFragment extends BaseFragment {

    @BindView(R.id.settings_savepass_options_recycler)
    RecyclerView recyclerView;

    SavePasswordOptionsAdapter adapter;

    @Inject
    SavePasswordService savePasswordService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_savepassword_options, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PasswordApplication.getSInjector(getActivity()).inject(this);

        ButterKnife.bind(this, getActivity());

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SavePasswordOptionsAdapter();

        adapter.addOption(getContext(), new SavePasswordOptionNever(getActivity(), savePasswordService));
        adapter.addOption(getContext(), new SavePasswordOptionAlways(getActivity(), savePasswordService));

        recyclerView.setAdapter(adapter);

    }

}
