package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 7/29/16.
 */
public class AutoLogoffServiceImplTimeShift extends AutoLogoffServiceImpl {

    private long timeShift = 0;

    public AutoLogoffServiceImplTimeShift(SessionService sessionService) {
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
