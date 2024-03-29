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
package com.intirix.cloudpasswordmanager.services.ui;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.PersistableBundle;

import javax.inject.Inject;

/**
 * Created by jeff on 7/29/16.
 */
public class ClipboardServiceImpl implements ClipboardService {

    private Context context;

    @Inject
    public ClipboardServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void copyStringToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        PersistableBundle extras = clip.getDescription().getExtras();
        if (extras==null) {
            extras = new PersistableBundle();
            extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
            clip.getDescription().setExtras(extras);
        } else {
            extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
        }
        clipboard.setPrimaryClip(clip);
    }
}
