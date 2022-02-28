package com.zhuli.repair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhuli.repair.LogInfo;
import com.zhuli.repair.RepairUtil;
import com.zhuli.repair.utils.AppUpdateUtil;
import com.zhuli.repair.utils.DownloadUtil;

/**
 * Copyright (C) 王字旁的理
 * Date: 2022/1/7
 * Description: 接收更新广播
 * Author: zl
 */
public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        LogInfo.e(intent.getAction());
        LogInfo.e(intent.getStringExtra(RepairUtil.updateVersion));
        LogInfo.e(intent.getIntExtra(RepairUtil.updateType, 0));
        LogInfo.e(intent.getStringExtra(RepairUtil.downloadUrl));
        LogInfo.e(intent.getStringExtra(RepairUtil.updateContent));

        if (RepairUtil.updateAction.equals(intent.getAction())) {

            String version = intent.getStringExtra(RepairUtil.updateVersion);
            if (AppUpdateUtil.appUpdate(context, version)) {
                LogInfo.e("服务器版本号大于本地版本：" + intent.getStringExtra(RepairUtil.updateVersion));
                DownloadUtil.downFile(context,
                        intent.getIntExtra(RepairUtil.updateType, -1),
                        intent.getStringExtra(RepairUtil.downloadUrl));

            }else {
                LogInfo.e("本地版本为最新版本，不需要更新：" + intent.getStringExtra(RepairUtil.updateVersion));
            }

        }
    }


}
