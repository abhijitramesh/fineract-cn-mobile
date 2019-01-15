/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package org.apache.fineract.data.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.apache.fineract.FineractApplication;
import org.apache.fineract.R;
import org.apache.fineract.data.local.PreferenceKey;
import org.apache.fineract.data.local.PreferencesHelper;

import java.io.IOException;
import java.util.HashSet;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

/**
 * @author Rajan Maurya
 * @since 17/03/2017
 */
public class FineractInterceptor implements Interceptor {

    public static final String HEADER_TENANT =String.valueOf(R.string.HEADER_TENANT);
    public static final String HEADER_AUTH = String.valueOf(R.string.HEADER_AUTH);
    private static final String HEADER_ACCEPT_JSON = String.valueOf(R.string.HEADER_ACCEPT_JSON);
    private static final String HEADER_CONTENT_TYPE = String.valueOf(R.string.HEADER_CONTENT_TYPE);
    public static final String HEADER_USER = String.valueOf(R.string.HEADER_USER);

    @Inject
    PreferencesHelper preferencesHelper;

    public FineractInterceptor(Context context) {
        FineractApplication.get(context).getComponent().inject(this);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request chainRequest = chain.request();
        Builder builder = chainRequest.newBuilder();

        //TODO fix call single time instead of calling every request
        String authToken = preferencesHelper.getAccessToken();
        String tenantIdentifier = preferencesHelper.getTenantIdentifier();
        String user = preferencesHelper.getUserName();
        Boolean refreshTokenStatus = preferencesHelper.getBoolean(
                PreferenceKey.PREF_KEY_REFRESH_ACCESS_TOKEN, false);

        builder.header(HEADER_ACCEPT_JSON, String.valueOf(R.string.Application_Json));
        builder.header(HEADER_CONTENT_TYPE, String.valueOf(R.string.Application_Json));

        if (refreshTokenStatus) {
            //Add Cookies
            HashSet<String> cookies = (HashSet<String>) preferencesHelper.getStringSet(
                            PreferenceKey.PREF_KEY_COOKIES);
            if (cookies != null) {
                for (String cookie : cookies) {
                    builder.addHeader(String.valueOf(R.string.Cookie), cookie);
                }
            }
        } else {
            if (!TextUtils.isEmpty(authToken)) {
                builder.header(HEADER_AUTH, authToken);
            }

            if (!TextUtils.isEmpty(user)) {
                builder.header(HEADER_USER, user);
            }
        }

        if (!TextUtils.isEmpty(tenantIdentifier)) {
            builder.header(HEADER_TENANT, tenantIdentifier);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}
