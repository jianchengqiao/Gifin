package com.sohu.inputmethod.sogou.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Qiao on 2018/12/27.
 */
public class DouTuTerminal implements Serializable {
    public List<RowsModel> rows;

    public static class RowsModel {
        public String url;
    }
}
