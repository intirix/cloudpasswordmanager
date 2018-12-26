package com.intirix.cloudpasswordmanager.services;

import android.content.Context;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import javax.inject.Inject;

public interface BiometricService {
    /**
     * Are we able to present the biometric prompt
     * @return
     */
    public boolean isBiometricPromptEnabled();

    /**
     * Is the user enrolled in biometric security
     * @return
     */
    public boolean isEnrolled();

    /**
     * Enroll in biometric authentication
     */
    public void enroll();

    /**
     * Unenroll in biometric authentication
     */
    public void unenroll();

    /**
     * Attempt to prompt the user for biometric authentication
     */
    public void promptForAuthentication();
}
