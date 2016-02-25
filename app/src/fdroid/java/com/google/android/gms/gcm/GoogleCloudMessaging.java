package com.google.android.gms.gcm;

import android.content.Context;

/**
 * Created by nocker on 14.02.16.
 */
public class GoogleCloudMessaging {
    public static GoogleCloudMessaging getInstance(Context applicationContext) {
        return new GoogleCloudMessaging();
    }

    public String register(String gcmSenderId) {
        return null;
    }
}
