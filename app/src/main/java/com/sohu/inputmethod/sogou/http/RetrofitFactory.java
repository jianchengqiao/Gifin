package com.sohu.inputmethod.sogou.http;

import com.sohu.inputmethod.sogou.util.LogUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitFactory {
    private static final int HTTP_CONNECT_TIMEOUT = 20000;
    private static final int HTTP_WRITE_TIMEOUT = 20000;
    private static final int HTTP_READ_TIMEOUT = 20000;
    private static final String TAG = "HTTP";
    private static Retrofit mJsonRetrofit;
    private static Retrofit mStringRetrofit;

    public static Retrofit createJsonRetrofit() {
        if (mJsonRetrofit == null) {
            synchronized (RetrofitFactory.class) {
                if (mJsonRetrofit == null) {
                    OkHttpClient.Builder okBuilder = (new OkHttpClient.Builder())
                            .addInterceptor(getLogInterceptor())
                            .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                            .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                            .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS);
                    OkHttpClient okHttpClient = okBuilder.build();
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .client(okHttpClient)
                            .baseUrl("http://focus.cn")
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create());
                    mJsonRetrofit = builder.build();
                }
            }
        }
        return mJsonRetrofit;
    }

    public static Retrofit createStringRetrofit() {
        if (mStringRetrofit == null) {
            synchronized (RetrofitFactory.class) {
                if (mStringRetrofit == null) {
                    OkHttpClient.Builder okBuilder = (new OkHttpClient.Builder())
                            .addInterceptor(getLogInterceptor())
                            .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                            .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                            .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS);
                    OkHttpClient okHttpClient = okBuilder.build();
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .client(okHttpClient)
                            .baseUrl("http://focus.cn")
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(ScalarsConverterFactory.create());
                    mStringRetrofit = builder.build();
                }
            }
        }
        return mStringRetrofit;
    }

    private static HttpLogInterceptor getLogInterceptor() {
        return new HttpLogInterceptor(message -> LogUtil.w(TAG, message)).setLevel(HttpLogInterceptor.Level.BODY);
    }
}
