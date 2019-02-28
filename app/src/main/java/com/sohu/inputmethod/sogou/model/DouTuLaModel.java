package com.sohu.inputmethod.sogou.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Qiao on 2018/12/27.
 */
public class DouTuLaModel implements Serializable {

    public DataModel data;

    public static class DataModel implements Serializable {
        public List<ListModel> list;

        public static class ListModel implements Serializable {
            public String image_url;
        }
    }
}
