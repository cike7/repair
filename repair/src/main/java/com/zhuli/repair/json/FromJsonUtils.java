package com.zhuli.repair.json;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @Description 工具类：json转换对象
 * @Author zhuli
 * @Date 2021/5/9/12:44 PM
 */
public class FromJsonUtils {

    public static <T> RequestResult<T> fromJson(String json, Class<T> clazz) {
        return new GsonBuilder()
                .registerTypeAdapter(RequestResult.class, new JsonFormatParser(clazz))
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create()
                .fromJson(json, RequestResult.class);
    }

    public static RequestResult fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, RequestResult.class);
    }

}
