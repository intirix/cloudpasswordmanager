package com.intirix.cloudpasswordmanager.services;

public class MockBiometricService implements BiometricService {
    private boolean enrolled = false;
    private boolean available = false;
    private boolean enrollCalled = false;
    private boolean unenrollCalled = false;
    private boolean promptCalled = false;

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean wasEnrollCalled() {
        return enrollCalled;
    }

    public boolean wasUnenrollCalled() {
        return unenrollCalled;
    }

    @Override
    public boolean isBiometricPromptEnabled() {
        return available;
    }

    @Override
    public boolean isEnrolled() {
        return enrolled;
    }

    @Override
    public void enroll() {
        enrollCalled = true;
    }

    @Override
    public void unenroll() {
        unenrollCalled = true;
    }

    @Override
    public void promptForAuthentication() {
        promptCalled = true;
    }
}
