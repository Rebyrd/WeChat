package com.WeChat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @projectName WeChat
 * @package     com.WeChat
 * @className:  MainActivity
 * @description 实现微信界面
 * @author      Rebyrd
 * @createDate  2021/10/25
 * @version     v0.02
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Tab message,contact,find,config;
    private ManagerTab managerTab;

    // 当前选中共的tab的linerLayout的ID
    private int currentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 构造tab对象
        message =new Tab(new Fragment_message() ,findViewById(R.id.tab_message),findViewById(R.id.txt_tab_message) ,findViewById(R.id.icon_message),getResources().getColor(R.color.color_selected),getResources().getColor(R.color.color_common));
        contact =new Tab(new Fragment_contact() ,findViewById(R.id.tab_contact),findViewById(R.id.txt_tab_contact) ,findViewById(R.id.icon_contact),getResources().getColor(R.color.color_selected),getResources().getColor(R.color.color_common));
        find    =new Tab(new Fragment_find()    ,findViewById(R.id.tab_find)   ,findViewById(R.id.txt_tab_find)    ,findViewById(R.id.icon_find)   ,getResources().getColor(R.color.color_selected),getResources().getColor(R.color.color_common));
        config  =new Tab(new Fragment_config()  ,findViewById(R.id.tab_config) ,findViewById(R.id.txt_tab_config)  ,findViewById(R.id.icon_config) ,getResources().getColor(R.color.color_selected),getResources().getColor(R.color.color_common));

        // 监听tab (本质上是监听tab的linerlayout)
        message .setOnClickListener(this);
        contact .setOnClickListener(this);
        find    .setOnClickListener(this);
        config  .setOnClickListener(this);

        // 初始化 tab管理器
        managerTab =new ManagerTab(R.id.content, Arrays.asList(message,contact,find,config),getSupportFragmentManager());
        managerTab.initManagerTab();
    }

    @Override
    public void onClick(View view) {
        // 扔掉冗余事件
        if(view.getId()==currentID) return;
        switch (view.getId()){
            case R.id.tab_message:
                managerTab.switchTab(message);
                currentID=R.id.tab_message;
                break;
            case R.id.tab_contact:
                managerTab.switchTab(contact);
                currentID=R.id.tab_contact;
                break;
            case R.id.tab_find:
                managerTab.switchTab(find);
                currentID=R.id.tab_find;
                break;
            case R.id.tab_config:
                managerTab.switchTab(config);
                currentID=R.id.tab_config;
                break;
        }
    }

    private static class Tab{
        private Fragment fragment;
        private LinearLayout linearLayout;
        private TextView textView;
        private ImageView imageView;
        /** 选中时-颜色 */
        private int getFocusedColor;
        /** 未选中时-颜色 */
        private int lostFocusedColor;

        public int getGetFocusedColor() {
            return getFocusedColor;
        }

        public int getLostFocusedColor() {
            return lostFocusedColor;
        }

        public void setGetFocusedColor(int getFocusedColor) {
            this.getFocusedColor = getFocusedColor;
        }

        public void setLostFocusedColor(int lostFocusedColor) {
            this.lostFocusedColor = lostFocusedColor;
        }

        /**
         * @param fragment tab中的fragment
         * @param linearLayout tab中的linerLayout
         * @param textView tab中的textView
         * @param imageView tab中的imageView
         */
        public Tab(Fragment fragment, LinearLayout linearLayout, TextView textView, ImageView imageView) {
            this.fragment = fragment;
            this.linearLayout = linearLayout;
            this.textView = textView;
            this.imageView = imageView;
        }

        /**
         * @param fragment tab中的fragment
         * @param linearLayout tab中的linerLayout
         * @param textView tab中的textView
         * @param imageView tab中的imageView
         * @param getFocusedColor tab选中时的颜色
         * @param lostFocusedColor tab未选中时的颜色
         */
        public Tab(Fragment fragment, LinearLayout linearLayout, TextView textView, ImageView imageView,int getFocusedColor,int lostFocusedColor) {
            this.fragment = fragment;
            this.linearLayout = linearLayout;
            this.textView = textView;
            this.imageView = imageView;
            this.getFocusedColor=getFocusedColor;
            this.lostFocusedColor=lostFocusedColor;
        }

        public void setOnClickListener(View.OnClickListener onClickListener){
            linearLayout.setOnClickListener(onClickListener);
        }

        /**
         * 被选中
         * @param fragmentManager 传入tab所在的fragmentManager
         */
        private void getFocused(FragmentManager fragmentManager){
            FragmentTransaction transaction= fragmentManager.beginTransaction();
            transaction.show(fragment);
            transaction.commit();
            switchColor(getFocusedColor);
        }

        /**
         * 移除选中
         * @param fragmentManager 传入tab所在的fragmentManager
         */
        private void lostFoucs(FragmentManager fragmentManager){
            FragmentTransaction transaction= fragmentManager.beginTransaction();
            transaction.hide(fragment);
            transaction.commit();
            switchColor (lostFocusedColor);
        }

        /**
         * 转换tab颜色
         * @param color 目标颜色
         */
        private void switchColor(int color) {
            textView.setTextColor(color);
            imageView.getDrawable().mutate().setTint(color);
        }
    }

    /** 用于管理tab行为，tab间交互 */
    private static class ManagerTab{
        /** 初始化 FragmentManager */
        private FragmentManager fragmentManager;
        /** 容器id */
        private int content;
        /** 处于同一个 FragmentManager 的 tab 集 */
        private List<Tab> tablist;
        private Tab currentTab;

        /**
         * @param content 传入fragment的容器
         * @param fragmentManager 传入fragment管理器
         */
        public ManagerTab(int content,FragmentManager fragmentManager) {
            tablist=new ArrayList<Tab>();
            this.content=content;
            this.fragmentManager=fragmentManager;
        }

        /**
         * @param content 传入fragment的容器
         * @param tabList 加入该管理器的tab集
         * @param fragmentManager 传入fragment管理器
         */
        public ManagerTab(int content,List<Tab> tabList,FragmentManager fragmentManager) {
            this.tablist=tabList;
            this.content=content;
            this.fragmentManager=fragmentManager;
        }

        /**
         * 向该管理器增加tab
         */
        public void add(Tab tab){
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.add(content,tab.fragment);
            transaction.commit();
            tablist.add(tab);
        }

        /**
         * 初始化管理器 <br/>
         * 必须调用，否则 管理器无法工作
         */
        private void initManagerTab() {
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            for(Tab tab:tablist){
                transaction.add(content,tab.fragment);
            }
            transaction.commit();
            hideAllTab();
            showTab(tablist.get(0));
        }

        /**
         * 隐藏所有tab
         */
        private void hideAllTab() {
            for(Tab tab:tablist){
                tab.lostFoucs(fragmentManager);
            }
        }

        /**
         * 选中该tab <BR/>
         * 注意:已经选中某个tab后使用该方法选中其他会导致多个tab选中,使fragment堆叠
         * @param tab 选中tab
         */
        private void showTab(Tab tab) {
            tab.getFocused(fragmentManager);
            currentTab=tab;
        }

        /**
         * 转换选中的tab
         * @param tab 选中该tab
         */
        private void switchTab(Tab tab) {
            currentTab.lostFoucs(fragmentManager);
            tab.getFocused(fragmentManager);
            currentTab=tab;
        }
    }
}