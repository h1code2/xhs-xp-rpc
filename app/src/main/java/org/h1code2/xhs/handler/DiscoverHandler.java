package org.h1code2.xhs.handler;

import com.google.gson.Gson;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.h1code2.xhs.utils.Store;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;

public class DiscoverHandler implements HttpServerRequestCallback {
    private final Gson gson = new Gson();

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        String uri = request.getQuery().getString("uri");
        Map<String, Object> map = new HashMap<>();
        map.put("data", null);
        if (uri == null || uri.equals("")) {
            map.put("code", -1);
            map.put("message", "uri is null");
            response.send(gson.toJson(map));
        }
        try {
            uri = URLDecoder.decode(uri, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            map.put("code", -1);
            map.put("message", e.getMessage());
            response.send(gson.toJson(map));
        }
        ClassLoader classLoader = Store.appClassLoader.get("xhs");
        Class<?> Routers = XposedHelpers.findClass(
                "com.xingin.android.xhscomm.router.Routers",
                classLoader
        );
        String newUri = String.format("xhsdiscover://%s", uri);
        Object routers = XposedHelpers.callStaticMethod(Routers, "build", newUri);
        XposedHelpers.callMethod(routers, "open", Store.appContext.get("xhs"));
        map.put("code", 0);
        map.put("message", newUri);
        response.send(gson.toJson(map));
    }
}
