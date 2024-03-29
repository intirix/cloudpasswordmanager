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
package com.intirix.cloudpasswordmanager.pages;


import android.content.Context;
import androidx.fragment.app.Fragment;
import android.util.Log;

/**
 * Created by jeff on 10/6/16.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();

    protected BaseActivity baseActivity;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, getClass().getSimpleName()+" onAttach()");
        super.onAttach(context);

        baseActivity = (BaseActivity)getActivity();
    }
}
