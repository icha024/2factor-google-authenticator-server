# GoogleAuth-TOTP

This is the server-side component for validating Google Authenticator's **Time-based One Time Password (TOTP)** as described in [RFC-6238](https://tools.ietf.org/html/rfc6238), commonly used for Two Factor Authentication (2FA).

The core logic is extracted from the Android version of [Google Authenticator](https://github.com/google/google-authenticator-android). This means you may fork the Google Authenticator project, or just download it from the app store, and it will work with that straight out the box!

## Packaged in two modules
- **Validator** package is a library component that exposed a simple method for validating the TOPT. This is handle if you wish you build your own 2FA server for validation.
- **Web** package is a pre-built, simple and high performance, micro-service using the TOTP validator library for you to deploy easily.
