package org.h1code2.xhs.utils;

import android.app.Activity;
import android.content.Context;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
    public static Map<String, ClassLoader> appClassLoader = new ConcurrentHashMap<>();
    public static Map<String, Context> appContext = new ConcurrentHashMap<>();
    public static Map<String, AsyncHttpServer> appAsyncHttpServer = new ConcurrentHashMap<>();
    public static Map<String, Activity> appActivity = new ConcurrentHashMap<>();
    public static Map<String, AsyncHttpServerResponse> appResponse = new ConcurrentHashMap<>();
    public static Map<String, Object> appObject = new ConcurrentHashMap<>();
    public static Map<String, Map<String, Object>> appTGResponse = new ConcurrentHashMap<>();
}
