package com.WeChat;

import static com.WeChat.ChatMsgPool.getGlobalInstance;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @projectName WeChat
 * @package     com.WeChat
 * @className:  MsgService
 * @description 网络服务，用于与后端进行数据交换，使用 OkHttp3实现
 * @author      Rebyrd
 * @createDate  2023/04/04
 * @version     v0.10
 */
public class MsgService extends Service {

    private OkHttpClient client;
    private Request.Builder builder;

    private static final String port = "8080";

    // IP 或 域名  测试后端
    private static final String hostname = "10.0.2.2";

    // 测试
    private static final String url = String.format("http://%s:%s",hostname,port);

    // session维持
    private static final String token = "3cd5d285-d75e-47b3-84e1-869dc2e5745b";

    private ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    private ChatMsgPool msgPool = getGlobalInstance();
    private PostBinder binder = new PostBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        Cookie cookie = new Cookie.Builder().name("token").value(token).domain(hostname).build();
        cookieStore.put(hostname,new ArrayList<Cookie>(){{
            add(cookie);
        }});

        client = new OkHttpClient.Builder().cookieJar(new CookieJar() { // 创建 cookie 管理
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies){ // cookie 存储
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url){ // cookie 读取
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();

        builder = new Request.Builder();

        Request request = builder.url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                try {
                    // 请求失败后 5 秒后重新发起请请求
                    Thread.sleep(5000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                client.newCall(request).enqueue(this);
                Log.d("Http Log","onFailure");
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                switch (response.code()){
                    case 200:
                        Map<String, Object> form = getForm(response.body().string());

                        String s = (String) form.get("sender");
                        String r = (String) form.get("recipent");
                        String msg = (String) form.get("message");
                        String t = (String) form.get("date");

                        Log.d("Http Log", String.format("Recv Msg: sender=%s recipent=%s text=%s", s, r, msg));

                        if (r != null && msg != null && s != null && t != null) {
                            if (r.equals(Contact.getmUID()) && Contact.contain(s))
                                msgPool.addRecvMsg(new ChatMsgPool.ChatMsg(s, r, msg, new Date(Long.valueOf(t)), ChatMsgPool.ChatMsg.Origin.REMOTE));
                        }
                        break;
                    case 404:
                        try {
                            // 无内容获取 5 秒后重新发起请请求
                            Thread.sleep(5000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            // 无内容获取 5 秒后重新发起请请求
                            Thread.sleep(5000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        Log.d("Http Log", String.format("Recv Error code %d",response.code()));
                }
                client.newCall(request).enqueue(this);
            }
        });
    }

    @Override
    public void onDestroy() {
        msgPool.save();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {return binder;}

    public class PostBinder extends Binder {

        public boolean post(String sender,String recipient,String message,Date date,Callback callback) {
            // 构造 Form 表单
            FormBody formBody = new FormBody.Builder().add("sender",sender).add("recipent",recipient).add("message",message).add("date", String.valueOf(date.getTime())).build();
            Request request = builder.url(url).post(formBody).build();

            client.newCall(request).enqueue(callback);
            return true;
        }
    }

    /**
     * 从String中 提取 Http表单
     * @param str Http Payload
     * @return Map格式表单（字典格式）
     */
    private Map<String,Object> getForm(String str){
        String[] splits = str.split("&");
        Map<String,Object> map = new HashMap<>();
        for(String split:splits){
            String[] key_value = split.split("=");
            if(key_value.length >= 2) {

                if (key_value[1].charAt(0) == '"' && key_value[1].charAt(key_value[1].length() - 1) == '"')
                    map.put(key_value[0], key_value[1].replace("\"",""));
                else
                    map.put(key_value[0], Integer.valueOf(key_value[1]));
            }
        }
        return map;
    }

}
