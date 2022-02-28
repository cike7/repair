package com.zhuli.repair;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.zhuli.repair.json.FromJsonUtils;
import com.zhuli.repair.json.RequestResult;
import com.zhuli.repair.network.NetworkManage;

import java.io.IOException;

import okhttp3.Response;


/**
 * Copyright (C) 王字旁的理
 * Date: 2022/1/7
 * Description: 修复代码，资源包，更新工具
 * Author: zl
 */
public class RepairUtil {

    public static final String updateAction = "com.zhuli.repair.receiver.UpdateReceiver";
    public static final String updateVersion = "update_version";
    public static final String updateType = "update_type";
    public static final String downloadUrl = "update_url";
    public static final String updateContent = "update_content";

    public static final int UPDATE_TYPE_REPAIR = 1;
    public static final int UPDATE_TYPE_RES = 2;
    public static final int UPDATE_TYPE_APK = 3;


    /**
     * 轮询更新
     *
     * @param context
     */
    public static void pollingUpdate(Context context, String url) {
        NetworkManage.send(url, response -> {
            if (response == null) return;
            if (response instanceof Response) {
                try {
                    String data = ((Response) response).body().string();
                    if (data == null || data.equals("") || data.length() < 1) return;
                    RequestResult<VersionModel> result = FromJsonUtils.fromJson(data, VersionModel.class);
                    RepairUtil.sendBroadcastUpdate(context, result.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 发送更新广播
     *
     * @param context
     * @param data
     */
    public static void sendBroadcastUpdate(Context context, VersionModel data) {
        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(updateAction);
        intent.putExtra(updateVersion, data.getVersion());
        intent.putExtra(updateType, data.getType());
        intent.putExtra(downloadUrl, data.getUrl());
        intent.putExtra(updateContent, data.getContent());
        intent.setComponent(new ComponentName(context.getPackageName(), updateAction));
        context.sendBroadcast(intent);
    }

}
