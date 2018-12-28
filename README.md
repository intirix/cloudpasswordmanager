# CloudPasswordManager

Android frontend for Cloud-based password managers

Backends
========

1. marius-wieschollek's Nextcloud Password Manager
   https://github.com/marius-wieschollek/passwords
   https://apps.nextcloud.com/apps/passwords
   Based on FCTurner's Owncloud Password Manager
   Uses Legacy API
2. Supports intirix's Serverless Secrets Manager
   https://github.com/intirix/serverless-secrets-manager
   Supports both Password and Encrypted RSA key authentication
3. Demo backend
   The demo backend is not actually used.  It allows you to explore a pre-defined
   password database just to see what the user interface looks like.  It is mostly
   used to enable the Google Play Pre-Flight report the ability to explore the app

Features
========

Biometric Login
---------------

This app makes use of the Android Pie (API 28) biometric login.  This API is newer,
but supports more types of biometric authentication.  When you enroll into using
biometric authentication, an AES256 key is randomly generated and stored onto
your device's trusted storage.  That key is used to encrypt your username and password.
That encrypted data is stored in the app's private area on your device.

Offline Mode
------------

An encrypted copy of your passwords can be stored on your device.  This cache
is stored in the app's private area.  It is encrypted using an AES256 key
that is generated using your username/password.  The Scrypt KDF is used to
generate the key.  Offline mode is opt-in, not opt-out.

Save Password
-------------

The app can save your password to make it easier to log in.  The password gets
encrypted with a random AES256 key.  The key gets stored in the app's preferences.
Please note that Android may not encrypt the app's preferences.  If you have Android
Jelly Bean or later (API 16), then the option will exist to only save the password
if the device is locked with a passcode.  Devices that are locked with a passcode
are more likely to encrypt the app's preferences.  For the most secure experience,
you should leave this feature disabled.
The Save Password feature is disabled by default.  You must opt into the feature.

Save Password Options:

1) Never (default)
2) Always (may not properly encrypt AES256 key that encrypted the password)
3) Only if device has a passcode
  1. Requires API 16
  2. More likely to encrypt the AES256 key that encrypted the password)

Certificate Pinning
------------

Normally, clients trust SSL/TLS certificates by walking up the certificate chain
until you find a CA that the client will trust.  There is an alternative method
for authenticating SSL certificates called **Certificate Pinning**.  When you *pin*
a certificate, you safe a copy of that certificate for the future.  The client
will only trust that certificate.  It won't even trust a certificate issued by
a valid CA.  Users can opt into pinning the certificate of their Cloud-based
password manager for a higher level of security.  Some advantages are:

1. Prevent rogue CA's from issuing certs
2. Prevent use of malicious CA's from being injected into your OS
3. Support self-signed certs for those that choose to not pay for a certificate

Get the App
===========

Google Play
-----------

https://play.google.com/store/apps/details?id=com.intirix.cloudpasswordmanager

#### Alpha version
Alpha release can be found in the Google Play store
https://play.google.com/apps/testing/com.intirix.cloudpasswordmanager