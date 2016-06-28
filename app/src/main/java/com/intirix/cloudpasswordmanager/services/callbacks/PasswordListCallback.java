package com.intirix.cloudpasswordmanager.services.callbacks;

import com.intirix.cloudpasswordmanager.services.beans.PasswordResponse;

import java.util.List;

/**
 * Created by jeff on 6/28/16.
 */
public interface PasswordListCallback extends BaseCallback {
    public void onReturn(List<PasswordResponse> passwords);
}
