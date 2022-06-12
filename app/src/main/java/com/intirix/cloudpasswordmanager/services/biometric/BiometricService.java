package com.intirix.cloudpasswordmanager.services.biometric;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

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
