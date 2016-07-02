package com.intirix.cloudpasswordmanager.events;

/**
 * Created by jeff on 6/28/16.
 */
public class FatalErrorEvent {

    private String message;

    public FatalErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
