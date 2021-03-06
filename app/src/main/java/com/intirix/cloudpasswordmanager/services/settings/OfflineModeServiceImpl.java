package com.intirix.cloudpasswordmanager.services.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.intirix.cloudpasswordmanager.pages.login.LoginFailedEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordadd.PasswordAddedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.SharedEncryptionService;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.inject.Inject;

public class OfflineModeServiceImpl implements OfflineModeService {

    private static final String TAG = OfflineModeServiceImpl.class.getSimpleName();

    private Context context;
    private SessionService sessionService;
    private SharedPreferences preferences;
    private SharedEncryptionService encryptionService;
    private EventService eventService;
    private boolean running = false;
    public static final String PREF_OFFLINE_MODE_SETTING = "OFFLINE_MODE_SETTING_KEY";

    @Inject
    public OfflineModeServiceImpl(Context context, SessionService sessionService, SharedEncryptionService encryptionService, EventService eventService) {
        this.context = context;
        this.sessionService = sessionService;
        this.encryptionService = encryptionService;
        this.eventService = eventService;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public boolean isDecryptionRunning() {
        return running;
    }

    @Override
    public boolean isOfflineModelEnabled() {
        final boolean ret = preferences.getBoolean(PREF_OFFLINE_MODE_SETTING, false);
        Log.d(TAG,"offline mode="+ret);
        return ret;
    }

    @Override
    public void enable() {
        Log.d(TAG,"Enabling offline mode");
        preferences.edit().putBoolean(OfflineModeServiceImpl.PREF_OFFLINE_MODE_SETTING,true).commit();
        updateOfflineModeCache(false);
    }

    @Override
    public void disable() {
        Log.d(TAG,"Disabling offline mode");
        preferences.edit().putBoolean(OfflineModeServiceImpl.PREF_OFFLINE_MODE_SETTING,false).commit();
        deleteCacheFile("passwords");
        deleteCacheFile("categories");
    }

    @Override
    public void updateOfflineModeCache(boolean foreground) {
        if (foreground) {
            Log.w(TAG,"updateOfflineModeCache() on main");
            updateOfflineModeCache();
        } else {
            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    updateOfflineModeCache();
                    return null;
                }
            }.execute();
        }

    }

    @Override
    public void loadDataFromCache(boolean foreground, final String username, final String password) {
        running = true;
        if (foreground) {
            Log.w(TAG,"loadDataFromCache() on main");

            loadDataFromCache(username,password);
        } else {
            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    loadDataFromCache(username,password);
                    return null;
                }
            }.execute();
        }
    }

    @Override
    public boolean isOfflineModeAvailable() {
        SessionInfo currentSession = sessionService.getCurrentSession();

        if (currentSession==null) {
            Log.d(TAG, "isOfflineModeAvailable(): no - there is not session");
            return false;
        }
        if (!isOfflineModelEnabled()) {
            Log.d(TAG, "isOfflineModeAvailable(): no - offline mode is disabled");
            return false;
        }
        if (currentSession.isPasswordBeanListEmpty()) {
            Log.d(TAG, "isOfflineModeAvailable(): no - no passwords available");
            return false;
        }
        return true;
    }

    private void updateOfflineModeCache() {
        Log.d(TAG,"updateOfflineModeCache()");
        SessionInfo currentSession = sessionService.getCurrentSession();

        List<PasswordBean> passwords = currentSession.getPasswordBeanList();
        byte[] key = encryptionService.keyExtendUsingScrypt(sessionService.getUsername(),sessionService.getCurrentSession().getPassword());
        saveObject(key,"passwords",currentSession.getPasswordBeanList());
        saveObject(key,"categories",currentSession.getCategoryList());
    }

    private void saveObject(byte[] key, String filename, Object obj) {
        File dir = context.getFilesDir();
        File file = new File(dir,filename);

        if (obj==null) {
            if (file.exists()) {
                // we should delete the file
                Log.d(TAG,"Deleting "+file.getAbsolutePath());
                file.delete();
            }
            return;
        }

        Log.d(TAG,"Writing "+file.getAbsolutePath());
        try {
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(
                        encryptionService.encryptStream(key,
                                new BufferedOutputStream(
                                        new FileOutputStream(file))));
                oos.writeObject(obj);
                oos.flush();
                Log.d(TAG,"Finished updating cache, file="+filename+", size="+file.length());
            } finally {
                if (oos != null) {
                    oos.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"Failed", e);
        }
    }

    private void loadDataFromCache(String username, String password) {
        Log.d(TAG,"loadDataFromCache()");
        SessionInfo currentSession = sessionService.getCurrentSession();

        byte[] key = encryptionService.keyExtendUsingScrypt(username,password);

        try {
            List<PasswordBean> passwordList = (List<PasswordBean>)decryptFile(currentSession, key,"passwords");
            if (passwordList!=null&&currentSession.isPasswordBeanListEmpty()) {
                Log.d(TAG,"Decrypted "+passwordList.size()+" passwords");
                currentSession.setPasswordBeanList(passwordList);

                // send the login as soon as a single successful decryption of a password happens
                Log.d(TAG,"loadDataFromCache(): Sending successful login event");
                eventService.postEvent(new LoginSuccessfulEvent());


                eventService.postEvent(new PasswordListUpdatedEvent());
            }
            List<Category> categoryList = (List<Category>)decryptFile(currentSession, key,"categories");
            if (categoryList!=null&&currentSession.isCategoryListEmpty()) {
                Log.d(TAG,"Decrypted "+categoryList.size()+" categories");
                currentSession.setCategoryList(categoryList);
                eventService.postEvent(new CategoryListUpdatedEvent());
            }

        } catch (Exception e) {
            Log.e(TAG,"Failed", e);
            eventService.postEvent(new LoginFailedEvent(e.getMessage()));
        } finally {
            running = false;
        }

    }

    private Object decryptFile(SessionInfo currentSession, byte[] key, String filename) throws Exception {
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        if (!file.exists()) {
            Log.d(TAG, "Cache does not exist: " + file.getAbsolutePath());
            return null;
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(
                    encryptionService.decryptStream(key,
                            new BufferedInputStream(
                                    new FileInputStream(file))));
            Object obj = ois.readObject();
            return obj;
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }

    private void deleteCacheFile(String filename) {
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        if (file.exists()) {
            file.delete();
        }
    }

    boolean doesCacheFileExist(String filename) {
        File dir = context.getFilesDir();
        File file = new File(dir, filename);
        return file.exists();
    }
}
