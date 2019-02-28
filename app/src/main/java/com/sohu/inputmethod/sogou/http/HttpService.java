package com.sohu.inputmethod.sogou.http;

import com.sohu.inputmethod.sogou.model.BeeModel;
import com.sohu.inputmethod.sogou.model.DouTuLaModel;
import com.sohu.inputmethod.sogou.model.DouTuTerminal;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Qiao.
 */

public interface HttpService {
    @GET("http://www.doutula.com/api/search")
    Single<DouTuLaModel> getFromDouTuLa(@Query("keyword") String key);

    @GET("http://www.52doutu.cn/api?types=search&action=searchpic")
    Single<DouTuTerminal> getFromTerminal(@Query("wd") String key, @Query("limit") int size);

    @GET("http://www.bee-ji.com/data/search/json?start=0")
    Single<ArrayList<BeeModel>> getFromBee(@Query("w") String key, @Query("size") int size, @Header("Cookie") String header);
}
