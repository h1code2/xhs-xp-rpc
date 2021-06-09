package org.h1code2.xhs.hook;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.koushikdutta.async.http.server.AsyncHttpServer;

import org.h1code2.xhs.handler.DiscoverHandler;
import org.h1code2.xhs.handler.PingHandler;
import org.h1code2.xhs.utils.Store;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Hook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.xingin.xhs")) {
            return;
        }
        XposedBridge.log("===================检测到小红书包名，开始hook============");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                final Context context = (Context) param.args[0];
                Store.appContext.put("xhs", context);
                ClassLoader classLoader = context.getClassLoader();
                Store.appClassLoader.put("xhs", classLoader);
//                pushNoteResponse(classLoader);
                XposedBridge.log("===================检测到小红书包名，准备开始注册服务");
                registerServer();
            }
        });
    }

    private void registerServer() {
        AsyncHttpServer server = Store.appAsyncHttpServer.get("xhs");
        if (server == null) {
            server = new AsyncHttpServer();
            Store.appAsyncHttpServer.put("xhs", server);
        }
        server.get("/ping", new PingHandler());
        server.get("/xhsdiscover", new DiscoverHandler());
        server.listen(5790);
        XposedBridge.log("xhs server registered!");
    }

    /**
     * 推送笔记内容 6.25.0
     *
     * @param classLoader
     */
    private void pushNoteResponse(final ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.xingin.skynet.c.d", classLoader, "a", "okhttp3.ResponseBody", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                final Object T = param.getResult();
                final String response = new Gson().toJson(T);
                XposedBridge.log(String.format("===================检测response %s", response));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pushResult(response);
                    }
                }).start();
            }
        });
    }

    private void pushResult(String data) {
        XposedBridge.log(String.format("===============data %s", data));
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        XposedBridge.log(String.format("===============JSON %s", JSON));
        RequestBody requestBody = RequestBody.create(JSON, data);
        XposedBridge.log(String.format("===============requestBody %s", requestBody));
        //创建一个请求对象
        Request request = new Request.Builder()
                .url("http://39.108.185.180:7553/push/")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                XposedBridge.log(String.format("===============onFailure %s", e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //打印服务端返回结果
                    assert response.body() != null;
                    XposedBridge.log(String.format("===============onResponse %s", response.body().toString()));
                }
            }
        });
    }
}