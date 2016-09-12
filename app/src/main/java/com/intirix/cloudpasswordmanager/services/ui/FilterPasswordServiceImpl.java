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

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by jeff on 8/17/16.
 */
public class FilterPasswordServiceImpl implements FilterPasswordService {

    private SessionService sessionService;

    @Inject
    public FilterPasswordServiceImpl(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public List<PasswordBean> filterPasswords(String text) {
        final String query = text.toLowerCase();
        // be safe
        if (sessionService.getCurrentSession()!=null) {

            // get the full list to filter
            final List<PasswordBean> fullList = sessionService.getCurrentSession().getPasswordBeanList();

            // if the filter is empty, then show everything
            if (query.length() == 0) {
                return fullList;
            } else {
                final List<PasswordBean> filteredList = new ArrayList<>(fullList.size());
                for (final PasswordBean bean : fullList) {
                    boolean add = matchesFilter(query, bean);

                    if (add) {
                        filteredList.add(bean);
                    }
                }
                return filteredList;
            }
        }
        return Collections.emptyList();
    }

    private boolean matchesFilter(String query, PasswordBean bean) {
        boolean add = false;

        add = add||bean.getWebsite()!=null && bean.getWebsite().toLowerCase().contains(query);
        add = add||bean.getAddress()!=null && bean.getAddress().toLowerCase().contains(query);
        add = add||bean.getLoginName()!=null && bean.getLoginName().toLowerCase().contains(query);
        add = add||bean.getNotes()!=null && bean.getNotes().toLowerCase().contains(query);
        add = add||bean.getCategoryName()!=null && bean.getCategoryName().toLowerCase().contains(query);
        return add;
    }
}
