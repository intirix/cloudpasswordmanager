package com.intirix.cloudpasswordmanager.services.session;

import com.intirix.cloudpasswordmanager.R;

/**
 * Created by jeff on 7/14/17.
 */

public enum StorageType {
    OWNCLOUD_PASSWORDS(R.string.storage_type_owncloud_passwords),
    SECRETS_MANAGER_API_V1(R.string.storage_type_secrets_manager_v1);

    private final int labelResource;

    private StorageType(int labelResource) {
        this.labelResource = labelResource;
    }

    public int getLabelResource() {
        return labelResource;
    }
}
