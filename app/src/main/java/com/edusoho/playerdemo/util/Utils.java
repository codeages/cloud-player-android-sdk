package com.edusoho.playerdemo.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.edusoho.playerdemo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int  MIN_CLICK_DELAY_TIME = 1000;
    private static       long lastClickTime;

    public static Map<String, String> getParams(Context context, String assets) {
        Map<String, String> params = new HashMap<>(2);
        try {
            InputStream    inputStream = context.getAssets().open(assets);
            StringBuilder  sb          = new StringBuilder();
            String         line;
            BufferedReader br          = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String str = sb.toString();
            params = new Gson().fromJson(str, new TypeToken<Map<String, String>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String getDefinitionStringName(Context context, String name) {
        String definitionName;
        switch (name) {
            case "SD":
                definitionName = context.getString(R.string.sd);
                break;
            case "HD":
                definitionName = context.getString(R.string.hd);
                break;
            case "SHD":
                definitionName = context.getString(R.string.shd);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + name);
        }
        return definitionName;
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.show();
    }

    public static boolean isFastClick() {
        boolean flag         = false;
        long    curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}
