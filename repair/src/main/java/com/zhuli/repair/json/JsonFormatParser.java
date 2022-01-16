package com.zhuli.repair.json;


import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @Description Json格式解析器
 * @Author zhuli
 * @Date 2021/5/9/12:35 PM
 */
public class JsonFormatParser implements JsonDeserializer<RequestResult> {

    private Class mClass;

    public JsonFormatParser(Class tClass) {
        this.mClass = tClass;
    }

    @Override
    public RequestResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // 根据Json元素获取Json对象。
        JsonObject mJsonObject = json.getAsJsonObject();
        //响应后端数据类
        RequestResult mResult = new RequestResult();
        // 根据键获取返回的状态码。
        mResult.setCode(mJsonObject.get("code").getAsInt());
        // 根据键获取返回的状态信息。
        mResult.setMessage(mJsonObject.get("message").getAsString());
        // 由于Json是以键值对的形式存在的，此处根据键(data)获取对应的Json字符串。
        String mJson = mJsonObject.get("data").toString();

        // 判断是Array还是Object类型。
        if (mJsonObject.get("data").isJsonArray() && !mJsonObject.get("data").isJsonNull()) {
            mResult.setData(fromJsonArray(mJson, mClass));
        } else if (mJsonObject.get("data").isJsonObject() && !mJsonObject.get("data").isJsonNull()) {
            mResult.setData(fromJsonObject(mJson, mClass));
        } else if (mJsonObject.get("data").isJsonPrimitive() && !mJsonObject.get("data").isJsonNull()) {
            // 服务端返回data的值为"{}","[]"，将对象或者集合以字符串的形式返回回来，先去除两边的双引号，再去掉转义字符。
            String mNewJson = mJson.substring(1, mJson.length() - 1).replaceAll("\\\\", "");
            // 根据处理好的Json字符串判断是集合还是对象，再进行解析。
            if (mNewJson.startsWith("[") || mNewJson.endsWith("]")) {
                mResult.setData(fromJsonArray(mNewJson, mClass));
            } else if (mNewJson.startsWith("{") || mNewJson.endsWith("}")) {
                mResult.setData(fromJsonObject(mNewJson, mClass));
            } else {
                mResult.setData(fromJsonObject(mResult.toString(), mClass));
            }
        } else if (mJsonObject.get("data").isJsonNull() || mJsonObject.get("data").getAsString().isEmpty()) {
            mResult.setData(fromJsonObject(mResult.toString(), String.class));
        }
        return mResult;
    }

    /**
     * 用来解析对象
     */
    private <T> T fromJsonObject(String json, Class<T> type) {
        return new Gson().fromJson(json, type);
    }

    /**
     * 用来解析集合
     */
    private <T> ArrayList<T> fromJsonArray(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);
        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

}
