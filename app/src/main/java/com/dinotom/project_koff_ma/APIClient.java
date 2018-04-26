package com.dinotom.project_koff_ma;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient
{
    private static Retrofit retrofit = null;

    public static Retrofit getClient()
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // adds automatic authorization headers for every request
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", "Token " + UserUtilities.getCurrentUserToken()).build();
                return chain.proceed(request);
            }
        });

        String apiURL = KoffGlobal.getAppContext().getResources().getString(R.string.rest_api_url);

        retrofit = new Retrofit.Builder()
                .baseUrl(apiURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }

    public static Retrofit getClientWithoutDefaultHeaders()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        String apiURL = KoffGlobal.getAppContext().getResources().getString(R.string.rest_api_url);

        retrofit = new Retrofit.Builder()
                .baseUrl(apiURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
