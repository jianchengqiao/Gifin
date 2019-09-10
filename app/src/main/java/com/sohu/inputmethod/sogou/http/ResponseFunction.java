package com.sohu.inputmethod.sogou.http;

import com.sohu.inputmethod.sogou.Constants;
import com.sohu.inputmethod.sogou.model.BeeModel;
import com.sohu.inputmethod.sogou.model.DouTuLaModel;
import com.sohu.inputmethod.sogou.model.SogouModel;
import com.sohu.inputmethod.sogou.util.LogUtil;
import com.sohu.inputmethod.sogou.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.functions.Function;

public class ResponseFunction<I> implements Function<I, ArrayList<String>> {

    public static final String DOUTULA_START = "http://img.doutula.com/production/uploads/image/";

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
        } else if (in instanceof String) {
            Pattern pattern = Pattern.compile(DOUTULA_START + "\\d{4}/\\d{2}/\\d{2}/\\d{14}_\\w{6}(\\.jpg|\\.jpeg|\\.gif|\\.png)");
            Matcher matcher = pattern.matcher((String) in);
            while (matcher.find()) {
                String group = matcher.group();
                if (strings.size() >= size) break;
                strings.add(group);
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
