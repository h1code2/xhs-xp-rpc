package org.h1code2.xhs.handler;

import com.google.gson.Gson;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.util.HashMap;
import java.util.Map;


public class PingHandler implements HttpServerRequestCallback {
    final private Gson gson = new Gson();

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", null);
        map.put("code", 0);
        map.put("message", "ping");
        response.send(gson.toJson(map));
    }
}