package com.sohu.inputmethod.sogou.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.sohu.inputmethod.sogou.App;
import com.sohu.inputmethod.sogou.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by Qiao on 2019/02/20.
 */
public class BackupUtil {

    private static final String BACKUP_FILE = "gifin_backup.property";

    public static void backup() {
        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.showToast("需要储存权限");
            return;
        }
        try {
            String favoriteList = SharedPrefUtil.getString(Constants.KEY_FAVORITE_LIST);
            String wordsList = SharedPrefUtil.getString(Constants.KEY_WORDS_LIST);
            Properties properties = new Properties();
            if (favoriteList != null && favoriteList.length() > 0) {
                properties.setProperty(Constants.KEY_FAVORITE_LIST, favoriteList);
            }
            if (wordsList != null && wordsList.length() > 0) {
                properties.setProperty(Constants.KEY_WORDS_LIST, wordsList);
            }
            FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), BACKUP_FILE));
            properties.store(out, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            ToastUtil.showToast("已备份");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void restore() {
        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.showToast("需要储存权限");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), BACKUP_FILE);
        if (!file.exists()) {
            ToastUtil.showToast("未发现备份文件");
            return;
        }
        try {
            Properties properties = new Properties();
            FileInputStream in = new FileInputStream(file);
            properties.load(in);
            String favoriteList = properties.getProperty(Constants.KEY_FAVORITE_LIST);
            String wordsList = properties.getProperty(Constants.KEY_WORDS_LIST);
            if (favoriteList != null && favoriteList.length() > 0) {
                SharedPrefUtil.putString(Constants.KEY_FAVORITE_LIST, favoriteList);
            }
            if (wordsList != null && wordsList.length() > 0) {
                SharedPrefUtil.putString(Constants.KEY_WORDS_LIST, wordsList);
            }
            ToastUtil.showToast("已恢复");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean needRestore() {
        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        File file = new File(Environment.getExternalStorageDirectory(), BACKUP_FILE);
        if (!file.exists()) {
            return false;
        }
        try {
            Properties properties = new Properties();
            FileInputStream in = new FileInputStream(file);
            properties.load(in);
            String favoriteList = properties.getProperty(Constants.KEY_FAVORITE_LIST);
            String wordsList = properties.getProperty(Constants.KEY_WORDS_LIST);
            in.close();
            String favoriteList2 = SharedPrefUtil.getString(Constants.KEY_FAVORITE_LIST);
            String wordsList2 = SharedPrefUtil.getString(Constants.KEY_WORDS_LIST);
            return ((favoriteList != null && favoriteList.length() > 0) || (wordsList != null && wordsList.length() > 0))
                    && (favoriteList2 == null || favoriteList2.length() == 0) && (wordsList2 == null || wordsList2.length() == 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
