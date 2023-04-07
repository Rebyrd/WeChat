package com.WeChat;

import static com.WeChat.ChatMsgPool.getGlobalInstance;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.WeChat.ChatMsgPool.ChatMsg;
import com.WeChat.ChatMsgPool.Observer;
import com.WeChat.MsgService.PostBinder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.Response;

/**
 * @projectName     WeChat
 * @package         com.WeChat
 * @className:      Context
 * @description     聊天界面
 * @author          Rebyrd
 * @recentModify    2023/04/04
 * @version         v0.10
 */
public class Context extends AppCompatActivity implements View.OnClickListener{
    private TextView name;

    private Button send;
    private EditText input;
    private ListView listView;

    private String UID;
    private ChatMsgListAdapter chatMsgListAdapter;

    private ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    private PostBinder postBinder;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            postBinder = (PostBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Observer observer = new Observer() {
        @Override
        public void execute(ChatMsg msg) {
            if(msg.getUID_sender().equals(UID) || msg.getUID_recipent().equals(UID)){
                // 滚动到最新消息
                MainActivity.runOnUIThread(()->{
                    chatMsgListAdapter.notifyDataSetChanged();
                    listView.setSelection(listView.getBottom());
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context);

        name=findViewById(R.id.top_txt);
        send = findViewById(R.id.btn_send);
        input = findViewById(R.id.txt_input);
        listView = findViewById(R.id.list_view);
        findViewById(R.id.ret_context).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        UID = intent.getStringExtra("UID");
        name.setText((String)Contact.getInfoByUID(UID).get("name"));

        send.setOnClickListener(this);

        chatMsgListAdapter = new ChatMsgListAdapter(this,UID);

        listView.setAdapter(chatMsgListAdapter);

        Intent service = new Intent(this,MsgService.class);
        bindService(service, conn, Context.BIND_AUTO_CREATE);

        // 绑定消息池观察对象
        getGlobalInstance().attachObserver(observer);
    }

    @Override
    protected void onDestroy() {
        // 解绑消息池观察对象
        getGlobalInstance().detachObserver(observer);
        unbindService(conn);
        super.onDestroy();
    }

    /**
     * 发送信息
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        String message = input.getText().toString();
        Date date = new Date();
        ChatMsg msg = new ChatMsg(Contact.getmUID(),UID,message,date, ChatMsg.Origin.LOCAL);
        msg.setStatus(ChatMsg.Status.WAITING);
        getGlobalInstance().addSendMsg(msg);

        postBinder.post(Contact.getmUID(), UID, message, date, new Callback() {
            private int index = getGlobalInstance().getSession(UID).indexOf(msg);

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                msg.setStatus(ChatMsg.Status.ERROR);
                View parent = listView.getChildAt(index);
                if(parent!=null)
                    ((ChatMsgListAdapter.ViewHolder) parent.getTag()).stopWaitingAnim(true);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                boolean isError = !response.isSuccessful();
                msg.setStatus(isError? ChatMsg.Status.ERROR: ChatMsg.Status.SUCCESSFUL);
                View parent = listView.getChildAt(index);
                if(parent!=null)
                    ((ChatMsgListAdapter.ViewHolder) parent.getTag()).stopWaitingAnim(isError);
            }
        });

        // 清空输入框
        input.setText("");
    }

    public String getUID() {
        return UID;
    }
}