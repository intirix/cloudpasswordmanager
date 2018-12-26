package com.intirix.cloudpasswordmanager.services.biometric;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.Toast;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.ErrorEvent;
import com.intirix.cloudpasswordmanager.services.SharedEncryptionService;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;

public class BiometricServiceImpl implements BiometricService {
    private static final String TAG = BiometricServiceImpl.class.getSimpleName();
    private Context context;
    private SharedEncryptionService sharedEncryptionService;
    private SessionService sessionService;
    private EventService eventService;

    // Unique identifier of a key pair
    private static final String KEY_NAME = "CPMKEY";

    /**
     * Callback that gets invoked during the enrollment flow
     */
    @TargetApi(Build.VERSION_CODES.P)
    private class BiometricEnrollmentCallback extends BiometricPrompt.AuthenticationCallback {
        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            try {
                // pass in the cipher to perform the encryption
                successfulEnrollment(result.getCryptoObject().getCipher());
                Toast.makeText(context, context.getString(R.string.settings_biometrics_enroll_success), Toast.LENGTH_LONG);
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
            }
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Toast.makeText(context,errString, Toast.LENGTH_LONG);
        }
    }

    /**
     * Callback that gets invoked during the authentication flow
     */
    @TargetApi(Build.VERSION_CODES.P)
    private class BiometricAuthenticationCallback extends BiometricPrompt.AuthenticationCallback {
        @Override
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            try {
                successfulAuthentication(result.getCryptoObject().getCipher());
                Toast.makeText(context, context.getString(R.string.settings_biometrics_auth_success), Toast.LENGTH_LONG);
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
            }
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Toast.makeText(context,errString, Toast.LENGTH_LONG);
        }
    }

    /**
     * Holder class that gets encrypted and stored
     */
    public static class BiometricHolder implements Serializable {
        private String username;
        private String password;
    }

    @Inject
    public BiometricServiceImpl(Context context,
                                SharedEncryptionService sharedEncryptionService,
                                SessionService sessionService,
                                EventService eventService) {
        this.context = context;
        this.sharedEncryptionService = sharedEncryptionService;
        this.sessionService = sessionService;
        this.eventService = eventService;
    }


    public boolean isBiometricPromptEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.d(TAG,"Biometric available, SDK="+Build.VERSION.SDK_INT);
            return true;
        } else {
            Log.d(TAG,"Biometric disabled because SDK "+Build.VERSION.SDK_INT+" is less than P("+Build.VERSION_CODES.P+")");
            return false;
        }
    }

    @Override
    public boolean isEnrolled() {
        if (!sharedEncryptionService.doesKeyExist(KEY_NAME)) {
            Log.d(TAG,"Not enrolled in biometric login because bio key is missing");
            return false;
        }
        if (!getBiometricDataFile().exists()) {
            Log.d(TAG,"biometric data file does not exist: "+getBiometricDataFile().getAbsolutePath());
            return false;
        }
        Log.d(TAG,"Biometric login should be available");
        return true;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.P)
    public void enroll() {
        try {
            // delete the old key if this is a re-enroll
            sharedEncryptionService.deleteKey(KEY_NAME);
            // generate a key that can only be used if authenticated
            SecretKey key = sharedEncryptionService.generateKey(KEY_NAME, true);

            // create the cipher
            Cipher cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            if (key != null) {
                byte[] iv = sharedEncryptionService.generateKey(SharedEncryptionService.AES_BLOCK_SIZE);
                cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

                // tell android to prompt for a biometric authentication
                // android has the same flow for enroll and auth.  The callback and cipher mode is what is different
                final BiometricEnrollmentCallback callback = new BiometricEnrollmentCallback();
                BiometricPrompt prompt = new BiometricPrompt.Builder(context)
                        .setTitle(context.getString(R.string.settings_biometrics_enroll_title))
                        .setNegativeButton(context.getString(R.string.settings_biometrics_enroll_cancel),
                                context.getMainExecutor(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG,"cancel");
                                    }
                                })
                        .build();
                prompt.authenticate(new BiometricPrompt.CryptoObject(cipher), getCancellationSignal(), context.getMainExecutor(), callback);
            }
        } catch (Exception e) {
            Log.e(TAG,"Failed to enroll in biometric login",e);
        }
    }

    private File getBiometricDataFile() {
        File dir = context.getFilesDir();
        File file = new File(dir,"biometricData.enc");
        return file;
    }

    void successfulEnrollment(Cipher cipher) throws Exception {
        BiometricHolder holder = new BiometricHolder();
        holder.username = sessionService.getUsername();
        holder.password = sessionService.getCurrentSession().getPassword();

        byte[] ivBytes = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();

        File file = getBiometricDataFile();

        Log.d(TAG,"Writing "+file.getAbsolutePath());
        try {
            ObjectOutputStream oos = null;
            try {
                BufferedOutputStream os = new BufferedOutputStream(
                        new FileOutputStream(file));
                os.write(ivBytes);


                oos = new ObjectOutputStream(
                        new CipherOutputStream(
                                os,cipher));
                oos.writeObject(holder);
                oos.flush();
                Log.d(TAG,"Finished updating cache, file="+"biometricData.enc"+", size="+file.length());
            } finally {
                if (oos != null) {
                    oos.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"Failed", e);
            return;
        }

        Log.d(TAG,"Successful enrollment");
    }

    public void successfulAuthentication(Cipher cipher) {
        File file = getBiometricDataFile();
        Log.d(TAG,"Decrypting "+file.getAbsolutePath());
        try {
            ObjectInputStream ois = null;
            try {
                BufferedInputStream is = new BufferedInputStream(
                        new FileInputStream(file));

                // the Cipher has already been initialized with the IV, but the stream still has it
                // we read in the IV just so that we can skip those bytes
                byte[] iv = new byte[SharedEncryptionService.AES_BLOCK_SIZE];
                if (is.read(iv,0, SharedEncryptionService.AES_BLOCK_SIZE)!=SharedEncryptionService.AES_BLOCK_SIZE) {
                    throw new IOException("Failed to skip the IV");
                }


                ois = new ObjectInputStream(
                        new CipherInputStream(
                                is, cipher));
                BiometricHolder holder = (BiometricHolder)ois.readObject();
                Log.d(TAG,"Successfully decrypted "+file.getAbsolutePath());
                sessionService.setUsername(holder.username);
                sessionService.start();
                sessionService.getCurrentSession().setPassword(holder.password);

                eventService.postEvent(new BiometricAuthenticationSuccessfulEvent());

            } finally {
                if (ois!=null) {
                    ois.close();
                }
            }
        } catch (Exception e) {
            Log.w(TAG,"Failed during post-biometric authentication",e);
            eventService.postEvent(new ErrorEvent(e.getMessage()));
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private CancellationSignal getCancellationSignal() {
        // With this cancel signal, we can cancel biometric prompt operation
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //handle cancel result
                Log.i(TAG, "Canceled");
            }
        });
        return cancellationSignal;
    }

    @Override
    public void unenroll() {
        Log.d(TAG,"unenroll()");
        try {
            sharedEncryptionService.deleteKey(KEY_NAME);
        } catch (Exception e) {
            Log.d(TAG,"Failed to delete the key",e);
        }
        File file = getBiometricDataFile();
        if (file.exists()) {
            Log.d(TAG,"Deleting "+file.getAbsolutePath());
            file.delete();
        }
    }


    @TargetApi(Build.VERSION_CODES.P)
    @Override
    public void promptForAuthentication() {
        try {
            Log.d(TAG,"promptForAuthentication()");
            SecretKey key = sharedEncryptionService.getSecretKey(KEY_NAME);
            Cipher cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            if (key != null) {
                byte[] iv = getIV();


                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

                final BiometricAuthenticationCallback callback = new BiometricAuthenticationCallback();
                BiometricPrompt prompt = new BiometricPrompt.Builder(context)
                        .setTitle(context.getString(R.string.settings_biometrics_auth_title))
                        .setNegativeButton(context.getString(R.string.settings_biometrics_auth_cancel),
                                context.getMainExecutor(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG,"cancel");
                                    }
                                })
                        .build();
                prompt.authenticate(new BiometricPrompt.CryptoObject(cipher), getCancellationSignal(), context.getMainExecutor(), callback);

            }
        } catch (Exception e) {
            Log.e(TAG,"Failed to authenticate",e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    /**
     * Get the IV from the biometric data file
     * @return
     * @throws IOException
     */
    private byte[] getIV() throws IOException {
        byte[] iv = new byte[SharedEncryptionService.AES_BLOCK_SIZE];
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(
                    new FileInputStream(getBiometricDataFile()));

            // read the iv
            if (is.read(iv, 0, SharedEncryptionService.AES_BLOCK_SIZE)!=SharedEncryptionService.AES_BLOCK_SIZE) {
                throw new IOException("Failed to read IV");
            }
        } finally {
            if (is!=null) {
                is.close();
            }
        }
        return iv;
    }
}
