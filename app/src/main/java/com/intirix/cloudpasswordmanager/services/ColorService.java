package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 7/16/16.
 */
public interface ColorService {

    public int parseColor(String hexrgb);

    public int getTextColorForBackground(int bg);
}
