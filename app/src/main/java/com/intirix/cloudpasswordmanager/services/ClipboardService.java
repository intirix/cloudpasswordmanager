package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 7/29/16.
 */
public interface ClipboardService {
    /**
     * Copy text to clipboard
     * @param label
     * @param text
     */
    public void copyStringToClipboard(String label, String text);
}
