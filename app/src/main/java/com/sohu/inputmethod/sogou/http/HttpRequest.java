package com.sohu.inputmethod.sogou.http;

import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Qiao.
 */

public class HttpRequest {

    private static HttpRequest mHttpRequest;

    private HttpRequest() {
    }

    public static HttpRequest getInstance() {
        if (mHttpRequest == null) {
            synchronized (HttpRequest.class) {
                if (mHttpRequest == null) {
                    mHttpRequest = new HttpRequest();
                }
            }
        }
        return mHttpRequest;
    }

    private <I> void subscribe(Single<I> single, ResponseSubscriber<ArrayList<String>> subscriber) {
        single.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ResponseFunction<I>())
                .subscribe(subscriber);
    }

    public void getGifList(String key, ResponseSubscriber<ArrayList<String>> subscriber) {
        int source = SharedPrefUtil.getInt(Constants.KEY_GIF_SOURCE, 1);
        if (source == 1) {
            getFromDouTuLa(key, subscriber);
        } else if (source == 2) {
            getFromTerminal(key, subscriber);
        } else if (source == 3) {
            getFromBee(key, subscriber);
        }
    }

    private void getFromBee(String key, ResponseSubscriber<ArrayList<String>> subscriber) {
        long time = System.currentTimeMillis() / 1000;
        int size = SharedPrefUtil.getInt(Constants.KEY_SETTING_PAGE_SIZE, 20);
        String cookie = String.format("Hm_lvt_65e796f34b9ee7170192209a91520a9a=%s; Hm_lpvt_65e796f34b9ee7170192209a91520a9a=%s", time, time);
        subscribe(RetrofitFactory.createJsonRetrofit().create(HttpService.class)
                .getFromBee(key, size, cookie), subscriber);
    }

    public void getFromDouTuLa(String key, ResponseSubscriber<ArrayList<String>> subscriber) {
        subscribe(RetrofitFactory.createJsonRetrofit().create(HttpService.class)
                .getFromDouTuLa(key), subscriber);
    }

    public void getFromTerminal(String key, ResponseSubscriber<ArrayList<String>> subscriber) {
        int size = SharedPrefUtil.getInt(Constants.KEY_SETTING_PAGE_SIZE, 20);
        subscribe(RetrofitFactory.createJsonRetrofit().create(HttpService.class)
                .getFromTerminal(key,size), subscriber);
    }
}