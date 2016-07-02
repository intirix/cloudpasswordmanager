package com.intirix.cloudpasswordmanager.events;

/**
 * Created by jeff on 6/28/16.
 */
public class ErrorEvent {

    private String message;

    public ErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
