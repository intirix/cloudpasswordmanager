/*
 * Copyright (C) 2016 Jeff Mercer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intirix.cloudpasswordmanager.services.ui;

import com.intirix.cloudpasswordmanager.services.ui.EventService;

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
