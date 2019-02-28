package com.sohu.inputmethod.sogou.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by Qiao on 2017/6/26.
 */

public class GsonUtil {

    private static final Gson GSON = new GsonBuilder().create();

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            T t = GSON.fromJson(json, new GsonType(classOfT, new Class[]{classOfT}));
            if (t == null) {
                t = classOfT.newInstance();
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> fromJsonList(String json, Class<T> classOfT) {
        try {
            return GSON.fromJson(json, new GsonType(List.class, new Class[]{classOfT}));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Map<String, T> fromJsonMap(String json, Class<T> classOfT) {
        try {
            return GSON.fromJson(json, new GsonType(Map.class, new Class[]{String.class, classOfT}));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GsonType implements ParameterizedType {
        private final Class raw;
        private final Type[] args;

        GsonType(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
