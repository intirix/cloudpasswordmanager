package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 7/29/16.
 */
public class AutoLogoffSessionImplTimeShift extends AutoLogoffSessionImpl {

    private long timeShift = 0;

    public AutoLogoffSessionImplTimeShift(SessionService sessionService) {
        super(sessionService);
    }

    public void addTimeShift(long t) {
        timeShift+=t;
    }

    @Override
    public long getCurrentTime() {
        return super.getCurrentTime() + timeShift;
    }
}
