package com.mqp.repair;

import java.io.File;

/**
 * @Description
 * @Author zhuli
 * @Time 2021/11/12 1:11
 */
public interface OnDownloadListener {

    /**
     * 下载成功之后的文件
     */
    void onDownloadSuccess(File file);

    /**
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载异常信息
     */

    void onDownloadFailed(Exception e);

}
