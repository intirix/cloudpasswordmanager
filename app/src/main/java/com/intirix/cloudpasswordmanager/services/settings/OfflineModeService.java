package com.intirix.cloudpasswordmanager.services.settings;

public interface OfflineModeService {

    /**
     * Is offline mode enabled?
     * @return
     */
    public boolean isOfflineModelEnabled();

    /**
     * Enable offline mode
     */
    public void enable();

    /**
     * Disable offline mode
     */
    public void disable();

    /**
     * Update the offline mode cache with the latest results
     */
    public void updateOfflineModeCache(boolean foreground);

    /**
     * Load the data from cache as if it was loaded from the server
     */
    public void loadDataFromCache(boolean foreground, String username, String password);

    /**
     * Is offline mode enabled and the data has been loaded
     * @return
     */
    public boolean isOfflineModeAvailable();

    /**
     * Is the decryption running still
     * @return
     */
    public boolean isDecryptionRunning();
}
