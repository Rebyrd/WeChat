package com.WeChat;

import static com.WeChat.ChatMsgPool.ChatMsg.formatDate;
import static com.WeChat.ChatMsgPool.getGlobalInstance;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.WeChat.ChatMsgPool.ChatMsg;
import com.WeChat.ChatMsgPool.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @projectName     WeChat
 * @package         com.WeChat
 * @className:      Fragment_message
 * @description     消息界面
 * @author          Rebyrd
 * @recentModify    2023/04/04
 * @version         v0.10
 */
public class Fragment_message extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private RecycleAdapter recycleAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // recyclerView的item内容
    private Map<String, List<Object>> resource;
    private Observer observer = new Observer() {
        @Override
        public void execute(ChatMsg msg) {
            String UID = msg.getOrigin() == ChatMsg.Origin.REMOTE ? msg.getUID_sender() : msg.getUID_recipent();

            Map<String, Object> info = Contact.getInfoByUID(UID);
            if (info != null) {
                if (!(Boolean) Contact.getInfoByUID(UID).get("show") || (Integer) info.get("msg_count") == 1) {
                    Contact.getInfoByUID(UID).put("show", true);
                    resource.get("UID").add(0, UID);
                    MainActivity.runOnUIThread(() -> {
                        recycleAdapter.insertItem((String) info.get("name"), msg.getText(), (int) info.get("header"), formatDate(msg.getDate()), 0);
                        recyclerView.smoothScrollToPosition(0);
                    });
                } else {
                    // runOnUiThread 会导致异步处理消息，因此强行将所有处理扔到UI线程执行（UI线程里面有Looper轮询，会重新同步数据处理）
                    MainActivity.runOnUIThread(() -> {
                        int index = resource.get("UID").indexOf(UID);
                        resource.get("context").set(index, foldStringByCharLength(msg.getText(), 40));
                        resource.get("time").set(index, formatDate(msg.getDate()));

                        if (index != 0) {
                            recycleAdapter.setTop(index);
                            List<Object> UIDs = resource.get("UID");
                            UIDs.add(0, UIDs.get(index));
                            UIDs.remove(index + 1);
                        }
                        recycleAdapter.notifyItemChanged(0);
                    });

                }
            }
        }
    };

    public RecycleAdapter getRecycleAdapter() {
        return recycleAdapter;
    }

    public Map<String, List<Object>> getResource() {
        return resource;
    }

    public Fragment_message() {
        // 初始化内容
        initItem();
    }

    public void initItem() {
        ChatMsgPool msgPool = getGlobalInstance();

        ArrayList<Object> UIDs = new ArrayList<Object>();
        ArrayList<Object> name = new ArrayList<Object>();
        ArrayList<Object> context = new ArrayList<Object>() {
            @Override
            public boolean add(Object o) {
//                if (null != o && ((String) o).length() > 22)
//                    o = ((String) o).substring(0, 22) + "···";
                return super.add(foldStringByCharLength((String)o,40));
            }
        };
        ArrayList<Object> header = new ArrayList<Object>();
        ArrayList<Object> date = new ArrayList<Object>();

        for (String UID : Contact.getKeys()) {
            ArrayList<ChatMsg> msgList = msgPool.getSession(UID);
            if (msgList != null && msgList.size() > 0 && (Boolean)Contact.getInfoByUID(UID).get("show")) {
                ChatMsg msg = msgList.get(msgList.size() - 1);
                UIDs.add(UID);
                name.add(Contact.getInfoByUID(UID).get("name"));
                context.add(msg.getText());
                header.add(Contact.getInfoByUID(UID).get("header"));
                date.add(formatDate(msg.getDate()));
            }
        }

        resource = new HashMap<>();
        resource.put("UID", UIDs);
        resource.put("name", name);
        resource.put("context", context);
        resource.put("image", header);
        resource.put("time", date);

        // 绑定消息池观察对象
        getGlobalInstance().attachObserver(observer);
    }

    @Override
    public void onDestroy() {
        // 解绑消息池观察对象
        getGlobalInstance().detachObserver(observer);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);


        // 初始化recyclerView
        recyclerView = view.findViewById(R.id.recyclerView_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // 初始化recycleAdapter
        recycleAdapter = new RecycleAdapter(view.getContext(),recyclerView, R.layout.recycle_message_item, resource);
        recyclerView.setAdapter(recycleAdapter);

        // 设置swipeRefresh区域
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    /**
     * swipeRefreh 回调
     * 在这里实现刷新逻辑
     */
    @Override
    public void onRefresh() {
        // 动态刷新时间 ms
        int delay = 2000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, delay);
    }

    public static String foldString(String str,int length){
        if (((String) str).length() > length - 3)
            str = ((String) str).substring(0, length -3) + "···";
        return str;
    }

    public static String foldStringByCharLength(String str,int length){
        int len = str.length();
        length -= 3;
        int index = 0;
        for(;index<len;index++){
            char ch = str.charAt(index);
            if(ch < 0x4E00 || ch > 0x9FA5) // 判断是不是汉字
                length--;
            else
                length -= 2;
            if(length<0)break;
        }
        if(length <0)
            return ((String) str).substring(0,index) + "...";
        else
            return str;
    }

}
