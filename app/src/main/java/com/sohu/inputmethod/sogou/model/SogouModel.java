package com.sohu.inputmethod.sogou.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Qiao on 2019-07-18.
 */
public class SogouModel implements Serializable {
    public List<ItemsModel> items;

    public static class ItemsModel implements Serializable {
        public String picUrl;
    }
}
