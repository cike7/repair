package com.zhuli.repair.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.zhuli.repair.LogInfo;

import java.lang.reflect.Method;

/**
 * 热修复
 */
public class FixResUtil {

    public static final String DEX_SUFFIX = ".dex";
    public static final String APK_SUFFIX = ".apk";
    public static final String JAR_SUFFIX = ".jar";
    public static final String ZIP_SUFFIX = ".zip";

    //解压路径文件名
    public static final String OPTIMIZE_DEX = "dex";
    public static final String OPTIMIZE_RES = "res";
    public static final String OPTIMIZE_APK = "apk";

    private static Resources mResources;

    public static Resources getResources(Context application, String apkPath) {
        if (mResources == null) {
            mResources = loadResources(application, apkPath);
        }
        return mResources;
    }

    public static Resources loadResources(Context context, String apkPath) {
        LogInfo.e("apkPath: " + apkPath);
        try {
            AssetManager assetManager = AssetManager.class.newInstance();

            Method addAssetPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);

            addAssetPathMethod.setAccessible(true);

            addAssetPathMethod.invoke(assetManager, apkPath);

            Resources resources = context.getResources();

            LogInfo.e("apkPath load ok");

            return new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}