package com.zhuli.repair.network;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.zhuli.repair.LogInfo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManage {

    //单个核线的fixed
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * POST
     *
     * @param url
     * @param params
     * @param callback
     */
    public static void send(String url, Map<String, String> params, NetworkCallback callback) {

        if (url == null || url.equals("")) return;

        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();

        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }

        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                sendCall(call, callback);
            }
        });

    }


    /**
     * GET
     *
     * @param url
     * @param callback
     */
    public static void send(String url, NetworkCallback callback) {

        if (url == null || url.equals("")) return;

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                sendCall(call, callback);
            }
        });
    }


    /**
     * 处理请求回调
     *
     * @param call
     * @param callback
     */
    private static void sendCall(Call call, NetworkCallback callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogInfo.e("请求失败," + e.getMessage());
                if (callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onCallback(e);
                        }
                    });
                }
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == 200) {
                    if (callback != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    callback.onCallback(response.body().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    LogInfo.e("请求错误," + response.code());
                }
            }
        });
    }

}
