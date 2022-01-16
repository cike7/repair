package com.zhuli.repair.utils;

import android.content.Context;

import com.zhuli.repair.LogInfo;
import com.zhuli.repair.RepairUtil;
import com.zhuli.repair.network.NetworkCallback;
import com.zhuli.repair.network.NetworkManage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;


/**
 * @Description 文件下载工具类
 * @Author zhuli
 * @Time 2021/11/12 1:11
 */
public class DownloadUtil {

    /**
     * 下载修复文件
     * Environment.getExternalStorageDirectory().getAbsolutePath()
     */
    public static void downFile(Context context, int type, String url) {
        //获取下载路径
        String path = FixDexUtil.getDownLoadPath(context, type);
        DownloadUtil download = new DownloadUtil();
        download.downLoadFile(url, type, path);
        download.setDownloadListener(new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String filePath) {

                LogInfo.e("下载完成后对应类型拿去解析" + filePath);

                if (type == RepairUtil.UPDATE_TYPE_REPAIR) {


                } else if (type == RepairUtil.UPDATE_TYPE_RES) {


                } else if (type == RepairUtil.UPDATE_TYPE_APK) {
                    AppUpdateUtil.installAPK(context, filePath);

                }

//                HashSet<File> loadedDex = FixDexUtil.isGoingToFix(new File(FixDexUtil.getDexPath()));
//
//                if (loadedDex != null) {
//                    FixDexManager manager = new FixDexManager(context);
//                    manager.doDexInject(loadedDex);
//                    FixDexUtil.doDexInject(context, loadedDex);
//                }

            }

            @Override
            public void onDownloading(int progress) {
                LogInfo.e("下载进度：" + progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                LogInfo.e("下载失败" + e.getMessage());
            }
        });
    }


    /**
     * 下载文件
     *
     * @param url
     * @param type
     * @param path
     */
    private void downLoadFile(String url, int type, String path) {

        final String fileName;

        if (type == RepairUtil.UPDATE_TYPE_REPAIR) {
            fileName = "classes.dex";

        } else if (type == RepairUtil.UPDATE_TYPE_RES) {
            fileName = "res.zip";

        } else if (type == RepairUtil.UPDATE_TYPE_APK) {
            fileName = "res.apk";

        } else {
            fileName = "";
        }

        Map<String, String> params = new HashMap<>();
        params.put("filename", fileName);
        NetworkManage.send(url, params, data -> {
            if (data instanceof Response) {
                LogInfo.e("下载完成,保存路径" + path + "/" + fileName);
                File file = new File(path + "/" + fileName);
                writeFile(file, (Response) data);
                if (downloadListener != null) {
                    downloadListener.onDownloadSuccess(file.getAbsolutePath());
                }
            } else if (data instanceof IOException) {
                LogInfo.e("下载失败");
                if (downloadListener != null) {
                    downloadListener.onDownloadFailed((IOException) data);
                }
            } else {
                if (downloadListener != null) {
                    downloadListener.onDownloadFailed(new Exception("请求无效！"));
                }
            }
        });

    }


    /**
     * 写入文件
     *
     * @param file
     * @param response
     */
    private void writeFile(File file, Response response) {
        OutputStream outputStream = null;
        InputStream inputStream = response.body().byteStream();
        try {
            outputStream = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private OnDownloadListener downloadListener;

    public void setDownloadListener(OnDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

}