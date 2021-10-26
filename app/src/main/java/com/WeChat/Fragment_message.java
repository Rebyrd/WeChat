package com.WeChat;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @projectName WeChat
 * @package     com.WeChat
 * @className:  Fragment_message
 * @description 消息界面
 * @author      Rebyrd
 * @createDate  2021/10/25
 * @version     v0.02
 */
public class Fragment_message extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private RecycleAdapter recycleAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // recyclerView的item内容
    private Map<String, List<Object>> resource;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
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

    public void initItem(){
        resource=new HashMap<>();
        resource.put("name",new ArrayList<Object>(){{
            add("路飞");
            add("鸣人");
            add("黑崎一护");
            add("黑子哲也");
            add("齐木楠雄");
            add("黑神目泷");
            add("日向翔阳");
            add("贝鲁");
        }});
        resource.put("context",new ArrayList<Object>(){{
            add("如果放弃我终生遗憾");
            add("不懂得重视同伴的人，是最最差劲的废物！");
            add("正是因为我们看不见,那才可怕。");
            add("虽然我是影子，但是光越强影就越浓。");
            add("不管是失败者多么厉害，都是赢家更夺人眼球。");
            add("不如说少年漫画对于我这样肤浅的人来说过于高深了。因为少年漫画教给读者的并不是友情、努力、胜利，而是有能力的人才能笑到最后，这样极其残酷的现实。因为有能力所以能交到朋友，因为有能力所以能努力，因为有能力所以能得到胜利。这样绝望的现实，我作为有能力的人都难以忍受啊！");
            add("你,如果是君临球场的王者的话,我会打倒你,成为站在球场上时间最长的人!");
            add("哒噗");
        }
        /**
         * 该内容长度限定
         */
        @Override
        public boolean add(Object o) {
            if(null!=o&&((String)o).length()>22)o=((String)o).substring(0,22)+"···";
            return super.add(o);
        }});
        resource.put("image",new ArrayList<Object>(){{
            add(R.mipmap.lufei);
            add(R.mipmap.mingren);
            add(R.mipmap.heizi);
            add(R.mipmap.yihu);
            add(R.mipmap.qimu);
            add(R.mipmap.meishen);
            add(R.mipmap.xiangyang);
            add(R.mipmap.beilu);
        }});
        resource.put("time",new ArrayList<Object>(){{
            add("12:10");
            add("12:00");
            add("11:43");
            add("11:40");
            add("10:32");
            add("13:12");
            add("12:36");
            add("10:57");
        }});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // 初始化recycleAdapter
        recycleAdapter=new RecycleAdapter(view.getContext(),R.layout.recycle_item,resource);

        // 初始化recyclerView
        RecyclerView recyclerView=view.findViewById(R.id.recyclerView_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(recycleAdapter);

        // 设置swipeRefresh区域
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // 添加空白内容（为了看到recyclerView的刷新）
        for(int i=0;i<4;i++){
            recycleAdapter.insertItem(null,null,0,null,-1);
        }
        return view;
    }

    /**
     * swipeRefreh 回调
     * 在这里实现刷新逻辑
     */
    @Override
    public void onRefresh() {
        // 动态刷新时间 ms
        int delay=2000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        },delay);
    }
}
