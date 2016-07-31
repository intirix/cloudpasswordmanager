package com.intirix.cloudpasswordmanager.services;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

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
        clipboard.setPrimaryClip(clip);
    }
}
