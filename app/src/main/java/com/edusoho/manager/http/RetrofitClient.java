package com.edusoho.manager.http;

import com.edusoho.manager.http.interceptor.RequestInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit.Builder   retrofitBuilder;
    private static RequestInterceptor mRequestInterceptor;

    public static Retrofit getInstance(String baseUrl, Map<String, String> headerMaps) {
        if (retrofitBuilder == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitBuilder == null) {
                    retrofitBuilder = new Retrofit.Builder()
                            .addConverterFactory(ToStringConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
                }
            }
        }
        mRequestInterceptor = new RequestInterceptor(headerMaps);

        retrofitBuilder.baseUrl(baseUrl).client(getClient());
        return retrofitBuilder.build();
    }

    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(mRequestInterceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
