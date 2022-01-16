package com.zhuli.repair.utils;

/**
 * @Description
 * @Author zhuli
 * @Time 2021/11/12 1:11
 */
public interface OnDownloadListener {

    /**
     * 下载成功之后的文件
     */
    void onDownloadSuccess(String filePath);

    /**
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载异常信息
     */
    void onDownloadFailed(Exception e);

}
