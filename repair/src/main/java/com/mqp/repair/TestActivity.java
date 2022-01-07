package com.mqp.repair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.HashSet;

/**
 * 手动检测并修复资源包
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FixDexUtil.init(this);
    }

    public void onClick() {
//        String url = "http://"+editText.getText().toString().trim().replace(" ","")+":8088/api/file/download";
//        String url = "http://127.0.0.1:8088/api/file/download";
        String url = "http://192.168.2.218:8088/api/file/download";
        System.out.println("TAG== " + url);
        downFile(url);
    }


    /**
     * 文件下载
     * Environment.getExternalStorageDirectory().getAbsolutePath()
     */
    private void downFile(String url) {
        DownloadUtil download = new DownloadUtil();
        download.downLoadFile(url, FixDexUtil.getDexPath() + "/classes1.dex");
        download.setDownloadListener(new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {

                HashSet<File> loadedDex = FixDexUtil.isGoingToFix(new File(FixDexUtil.getDexPath()));

                if (loadedDex != null) {
                    FixDexManager manager = new FixDexManager(TestActivity.this);
//                    manager.doDexInject(loadedDex);
                    FixDexUtil.doDexInject(TestActivity.this,loadedDex);
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed(Exception e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
