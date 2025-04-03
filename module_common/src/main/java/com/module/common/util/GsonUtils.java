package com.module.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;


public class GsonUtils {

    public static <T>T json2VO(String json, Class<T> c){
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(json, c);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T>T json2VO(String json, Type c){
        try {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(json, c);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String vo2Json(Object obj){
        try {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(obj);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }
}
