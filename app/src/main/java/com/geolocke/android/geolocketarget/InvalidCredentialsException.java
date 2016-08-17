package com.geolocke.android.geolocketarget;

public class InvalidCredentialsException extends Exception {

    private final String mApplicationKey;
    private final String mDeveloperKey;


    public InvalidCredentialsException(String message, String pApplicationKey, String pDeveloperKey ) {
        super(message); this.mApplicationKey = pApplicationKey; this.mDeveloperKey = pDeveloperKey;
    }

    public InvalidCredentialsException(String message, String pApplicationKey, String pDeveloperKey , Throwable cause) {
        super(message, cause);
        this.mApplicationKey = pApplicationKey;
        this.mDeveloperKey = pDeveloperKey;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " for ApplicationKey: " + mApplicationKey + " and DeveloperKey: "+ mDeveloperKey;
    }

}

