package com.mqp.repair;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * @Description 文件下载工具类
 * @Author zhuli
 * @Time 2021/11/12 1:11
 */
public class DownloadUtil {

    private OnDownloadListener downloadListener;

    public void downLoadFile(String url,String path) {

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("filename","classes1.dex")
                .build();

        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG==", "热修复,下载失败" + e.getMessage());
                if(downloadListener != null){
                    downloadListener.onDownloadFailed(e);
                }
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200){
                    Log.e("TAG==", "下载完成,保存路径" + path);
                    File file = new File(path);
                    writeFile(file,response);
                    if(downloadListener != null){
                        downloadListener.onDownloadSuccess(file);
                    }
                }else {
                    if(downloadListener != null){
                        downloadListener.onDownloadFailed(new Exception("请求无效！"));
                    }
                }
            }
        });
    }


    /**
     * 写入文件
     * @param file
     * @param response
     */
    private void writeFile(File file,Response response) {
        OutputStream outputStream = null;
        InputStream inputStream = response.body().byteStream();
        try {
            outputStream = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream != null)
                    inputStream.close();
                if(outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDownloadListener(OnDownloadListener downloadListener){
        this.downloadListener = downloadListener;
    }

}