package com.sohu.inputmethod.sogou.http;

import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.model.BeeModel;
import com.sohu.inputmethod.sogou.model.DouTuLaModel;
import com.sohu.inputmethod.sogou.model.DouTuTerminal;
import com.sohu.inputmethod.sogou.model.SogouModel;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;

import java.util.ArrayList;

import io.reactivex.functions.Function;

public class ResponseFunction<I> implements Function<I, ArrayList<String>> {
    @Override
    public ArrayList<String> apply(I in) {
        ArrayList<String> strings = new ArrayList<>();
        int size = SharedPrefUtil.getInt(Constants.KEY_SETTING_PAGE_SIZE, 20);
        if (in instanceof ArrayList) {
            if (((ArrayList) in).size() > 0) {
                for (int i = 0; i < size && i < ((ArrayList) in).size(); i++) {
                    Object model = ((ArrayList) in).get(i);
                    if (model instanceof BeeModel) {
                        strings.add(String.format("http://image.bee-ji.com/%s", ((BeeModel) model).id));
                    }
                }
            }
        } else if (in instanceof DouTuTerminal) {
            if (((DouTuTerminal) in).rows != null && ((DouTuTerminal) in).rows.size() > 0) {
                for (int i = 0; i < size && i < ((DouTuTerminal) in).rows.size(); i++) {
                    strings.add(((DouTuTerminal) in).rows.get(i).url);
                }
            }
        } else if (in instanceof DouTuLaModel) {
            if (((DouTuLaModel) in).data != null && ((DouTuLaModel) in).data.list != null && ((DouTuLaModel) in).data.list.size() > 0) {
                for (int i = 0; i < size && i < ((DouTuLaModel) in).data.list.size(); i++) {
                    strings.add(((DouTuLaModel) in).data.list.get(i).image_url);
                }
            }
        } else if (in instanceof SogouModel) {
            if (((SogouModel) in).items != null && ((SogouModel) in).items != null && ((SogouModel) in).items.size() > 0) {
                for (int i = 0; i < size && i < ((SogouModel) in).items.size(); i++) {
                    strings.add(((SogouModel) in).items.get(i).picUrl);
                }
            }
        }
        return strings;
    }
}
