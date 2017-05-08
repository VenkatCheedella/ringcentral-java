package com.ringcentral;

import com.alibaba.fastjson.JSON;
import com.ringcentral.definitions.TokenInfo;
import io.mikael.urlbuilder.UrlBuilder;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class RestClient {
    public static final String SANDBOX_SERVER = "https://platform.devtest.ringcentral.com";
    public static final String PRODUCTION_SERVER = "https://platform.ringcentral.com";
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
    public boolean autoRefresh = true;
    private String appKey;
    private String appSecret;
    private String server;
    private TokenInfo _token;
    private Timer timer;

    public RestClient(String appKey, String appSecret, String server) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.server = server;
    }

    public TokenInfo getToken() {
        return _token;
    }

    public void setToken(TokenInfo token) {
        _token = token;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (autoRefresh && token != null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refresh();
                }
            }, (token.expires_in - 120) * 1000);
        }
    }

    public void authorize(String username, String extension, String password) throws IOException, RestException {
        FormBody formBody = new FormBody.Builder()
            .add("grant_type", "password")
            .add("username", username)
            .add("extension", extension)
            .add("password", password)
            .build();
        setToken(null);
        setToken(post("/restapi/oauth/token", formBody, TokenInfo.class));
    }

    public void authorize(String auth_code, String redirectUri) throws IOException, RestException {
        FormBody formBody = new FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", auth_code)
            .add("redirect_uri", redirectUri)
            .build();
        setToken(null);
        setToken(post("/restapi/oauth/token", formBody, TokenInfo.class));
    }

    public void refresh() {
        if (getToken() == null) {
            return;
        }
        FormBody formBody = new FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", getToken().refresh_token)
            .build();
        setToken(null);
        try {
            setToken(post("/restapi/oauth/token", formBody, TokenInfo.class));
        } catch (IOException | RestException e) {
            e.printStackTrace();
        }
    }

    public void revoke() throws IOException, RestException {
        if (getToken() == null) {
            return;
        }
        FormBody formBody = new FormBody.Builder()
            .add("token", getToken().access_token)
            .build();
        setToken(null);
        post("/restapi/oauth/revoke", formBody);
    }

    public String authorizeUri(String redirectUri, String state) {
        return UrlBuilder.fromString(server)
            .withPath("/restapi/oauth/authorize")
            .addParameter("response_type", "code")
            .addParameter("state", state)
            .addParameter("redirect_uri", redirectUri)
            .addParameter("client_id", appKey)
            .toString();
    }

    public String authorizeUri(String redirectUri) {
        return authorizeUri(redirectUri, "");
    }

    public Subscription subscription(String[] events, Consumer<String> callback) {
        return new Subscription(this, events, callback);
    }

    private String basicKey() {
        return new String(Base64.getEncoder().encode(MessageFormat.format("{0}:{1}", appKey, appSecret).getBytes()));
    }

    private String authorizationHeader() {
        if (getToken() != null) {
            return MessageFormat.format("Bearer {0}", getToken().access_token);
        }
        return MessageFormat.format("Basic {0}", basicKey());
    }

    public String request(Request request) throws IOException, RestException {
        Response response = httpClient.newCall(request).execute();
        int statusCode = response.code();
        if (statusCode < 200 || statusCode > 299) {
            throw new RestException(statusCode, response.body().string());
        }
        return response.body().string();
    }

    public String get(String endpoint) throws IOException, RestException {
        Request request = new Request.Builder().url(server + endpoint)
            .addHeader("Authorization", authorizationHeader()).build();
        return request(request);
    }

    public String post(String endpoint, FormBody formBody) throws IOException, RestException {
        Request request = new Request.Builder().url(server + endpoint)
            .addHeader("Authorization", authorizationHeader()).post(formBody).build();
        return request(request);
    }

    public String post(String endpoint, Object base) throws IOException, RestException {
        RequestBody body = RequestBody.create(jsonMediaType, JSON.toJSONString(base));
        Request request = new Request.Builder().url(server + endpoint)
            .addHeader("Authorization", authorizationHeader()).post(body).build();
        return request(request);
    }

    public String put(String endpoint, Object base) throws IOException, RestException {
        RequestBody body = RequestBody.create(jsonMediaType, JSON.toJSONString(base));
        Request request = new Request.Builder().url(server + endpoint)
            .addHeader("Authorization", authorizationHeader()).put(body).build();
        return request(request);
    }

    public void delete(String endpoint) throws IOException, RestException {
        Request request = new Request.Builder().url(server + endpoint)
            .addHeader("Authorization", authorizationHeader()).delete().build();
        request(request);
    }

    public <T> T get(String endpoint, Type t) throws IOException, RestException {
        String jsonString = get(endpoint);
        return JSON.parseObject(jsonString, t);
    }

    public <T> T post(String endpoint, FormBody formBody, Type t) throws IOException, RestException {
        String jsonString = post(endpoint, formBody);
        return JSON.parseObject(jsonString, t);
    }

    public <T> T post(String endpoint, Object base, Type t) throws IOException, RestException {
        String jsonString = post(endpoint, base);
        return JSON.parseObject(jsonString, t);
    }

    public <T> T put(String endpoint, Object base, Type t) throws IOException, RestException {
        String jsonString = put(endpoint, base);
        return JSON.parseObject(jsonString, t);
    }
}
