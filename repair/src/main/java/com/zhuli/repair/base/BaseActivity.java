package com.zhuli.repair.base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextThemeWrapper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhuli.repair.LogInfo;
import com.zhuli.repair.utils.FixResUtil;

import java.io.File;
import java.lang.reflect.Field;

public class BaseActivity extends AppCompatActivity {

    private Context mContext;
    protected Resources mResources;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String apkNewPath = getExternalFilesDir("").getAbsolutePath() + File.separator + "debug.apk";
        mResources = FixResUtil.getResources(getApplication(), apkNewPath);
        if (mResources != null) {
            mContext = new ContextThemeWrapper(getBaseContext(), 0);
            Class<? extends Context> clazz = mContext.getClass();
            try {
                Field mResourcesField = clazz.getDeclaredField("mResources");
                mResourcesField.setAccessible(true);
                mResourcesField.set(mContext, mResources);

                LogInfo.e("mResources 替换完成！");

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
