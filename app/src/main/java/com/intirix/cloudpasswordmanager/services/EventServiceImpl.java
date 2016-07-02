package com.intirix.cloudpasswordmanager.services;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jeff on 6/28/16.
 */
public class EventServiceImpl implements EventService {
    @Override
    public void postEvent(Object event) {
        EventBus.getDefault().post(event);
    }
}
