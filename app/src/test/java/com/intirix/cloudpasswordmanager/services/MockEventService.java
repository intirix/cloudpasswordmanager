package com.intirix.cloudpasswordmanager.services;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/29/16.
 */
public class MockEventService implements EventService {

    private final List<Object> events = new ArrayList<>();

    @Override
    public void postEvent(Object event) {
        events.add(event);
    }

    public int getPostedCount() {
        return events.size();
    }

    /**
     * Check the event type
     * @param i
     * @param klass
     */
    public void assertEventType(int i, Class<?> klass) {
        Assert.assertTrue(klass.isAssignableFrom(events.get(i).getClass()));
    }

    /**
     * Check the number of events that we posted
     * @param i
     */
    public void assertNumberOfPosts(int i) {
        Assert.assertEquals(i, events.size());
    }

    /**
     * Get the event
     * @param i
     * @param klass
     * @param <T>
     * @return
     */
    public <T> T getEvent(int i, Class<T> klass) {
        return (T)events.get(i);
    }
}
