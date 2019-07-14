package com.intirix.cloudpasswordmanager.pages.login;

public class LoginFailedEvent {
    private String message;

    public LoginFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
