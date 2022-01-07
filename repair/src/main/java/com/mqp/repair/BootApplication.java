package com.mqp.repair;

import android.app.Application;
import android.content.Context;

/**
 * @Description 启动app入口
 * @Author zhuli
 * @Time 2021/11/11 23:58
 */
public class BootApplication extends Application {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void attachBaseContext(Context base) {
        FixDexUtil.init(base);
//        HashSet<File> loadedDex = FixDexUtil.isGoingToFix(new File(FixDexUtil.getDexPath()));
//        if (loadedDex != null) {
//            FixDexUtil.doDexInject(base,loadedDex);
//        }
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
