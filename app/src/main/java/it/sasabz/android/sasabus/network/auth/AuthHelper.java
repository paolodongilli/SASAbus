package it.sasabz.android.sasabus.network.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.DataInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import it.sasabz.android.sasabus.network.auth.jjwt.Claims;
import it.sasabz.android.sasabus.network.auth.jjwt.Jws;
import it.sasabz.android.sasabus.network.auth.jjwt.Jwts;
import it.sasabz.android.sasabus.network.auth.jjwt.SignatureException;
import it.sasabz.android.sasabus.util.LogUtils;
import retrofit2.adapter.rxjava.HttpException;

public final class AuthHelper {

    private static PublicKey publicKey;

    private static final String TAG = "AuthHelper";

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    private AuthHelper() {
    }

    private static final String PREF_AUTH_TOKEN = "pref_auth_token";
    private static final String PREF_USER_ID = "pref_user_id";

    public static void init(Context context) {
        sContext = context;

        publicKey = getPublicKey();
    }

    private static PublicKey getPublicKey() {
        try {
            InputStream fileInputStream = sContext.getAssets().open("keys/public_key.der");
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            byte[] keyBytes = new byte[fileInputStream.available()];
            dataInputStream.readFully(keyBytes);
            dataInputStream.close();

            KeySpec encodedKeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory instance = KeyFactory.getInstance("RSA");

            return instance.generatePublic(encodedKeySpec);
        } catch (Exception e) {
            throw new RuntimeException("Could not load key", e);
        }
    }

    public static void checkIfUnauthorized(Activity activity, Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;

            if (httpException.code() == 401) {
                LogUtils.e(TAG, "Unauthorized response, clearing credentials");

                clearCredentials();

                // TODO: 28/07/16 Redirect to login once that has been added.
                //activity.finish();
                //activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        }
    }


    // ====================================== PREFERENCES ==========================================

    @Nullable
    private static String getUserId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_USER_ID, null);
    }

    private static void setUserId(Context context, String userId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_USER_ID, userId).apply();
    }

    @Nullable
    private static String getAuthToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_AUTH_TOKEN, null);
    }

    private static void setAuthToken(Context context, String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_AUTH_TOKEN, token).apply();
    }


    // ====================================TOKEN VERIFICATION ======================================

    @Nullable
    public static String getTokenIfValid() {
        String token = getAuthToken(sContext);

        if (token == null) {
            return null;
        }

        if (verifyToken(token)) {
            return token;
        }

        return null;
    }

    public static boolean setInitialToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token);

            String userId = claims.getBody().getSubject();

            if (TextUtils.isDigitsOnly(userId)) {
                LogUtils.e(TAG, "User id is empty");

                clearCredentials();

                return false;
            }

            LogUtils.d(TAG, "Token is valid, got user id: " + userId);

            setUserId(sContext, userId);
            setAuthToken(sContext, token);

            return true;
        } catch (SignatureException e) {
            e.printStackTrace();

            clearCredentials();

            Log.e(TAG, "Key is invalid, clearing credentials");

            return false;
        }
    }

    public static boolean verifyToken() {
        String token = getAuthToken(sContext);

        return !TextUtils.isEmpty(token) && verifyToken(token);
    }

    private static boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token);

            String userId = claims.getBody().getSubject();
            String savedUserId = getUserId(sContext);

            if (TextUtils.isEmpty(savedUserId)) {
                LogUtils.e(TAG, "Saved user id is empty");
                clearCredentials();
                return false;
            }

            if (TextUtils.isEmpty(userId)) {
                LogUtils.e(TAG, "Token user id is empty");
                clearCredentials();
                return false;
            }

            if (!userId.equals(savedUserId)) {
                LogUtils.e(TAG, "Saved user id and token user id don't match, should be " +
                        savedUserId + ", got " + userId + " instead");

                clearCredentials();

                return false;
            }

            LogUtils.d(TAG, "Token is valid, got user id: " + userId);

            return true;
        } catch (SignatureException e) {
            e.printStackTrace();

            clearCredentials();

            Log.e(TAG, "Key is invalid, clearing credentials");

            return false;
        }
    }

    private static void clearCredentials() {
        setUserId(sContext, null);
        setAuthToken(sContext, null);
    }
}
