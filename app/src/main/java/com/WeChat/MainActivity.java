package com.WeChat;

import android.graphics.Color;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout tab_message, tab_contact,tab_find,tab_config;
    private Tab message,contact,find,config;
    private ManagerTab managerTab;
    private int currentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tab_message = findViewById(R.id.tab_message);
        tab_contact = findViewById(R.id.tab_contact);
        tab_find    = findViewById(R.id.tab_find);
        tab_config  = findViewById(R.id.tab_config);

        tab_message .setOnClickListener(this);
        tab_contact .setOnClickListener(this);
        tab_find    .setOnClickListener(this);
        tab_config  .setOnClickListener(this);

        message =new Tab(new fragment_message() ,tab_message,findViewById(R.id.txt_tab_message) ,findViewById(R.id.icon_message),R.color.selected,R.color.common);
        contact =new Tab(new fragment_contact() ,tab_contact,findViewById(R.id.txt_tab_contact) ,findViewById(R.id.icon_contact),R.color.selected,R.color.common);
        find    =new Tab(new fragment_find()    ,tab_find   ,findViewById(R.id.txt_tab_find)    ,findViewById(R.id.icon_find)   ,R.color.selected,R.color.common);
        config  =new Tab(new fragment_config()  ,tab_config ,findViewById(R.id.txt_tab_config)  ,findViewById(R.id.icon_config) ,R.color.selected,R.color.common);


        managerTab =new ManagerTab(R.id.content, Arrays.asList(message,contact,find,config));
        managerTab.initManagerTab();
    }

    @Override
    public void onClick(View view) {
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
        private int getFocusedColor;
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

        public Tab(Fragment fragment, LinearLayout linearLayout, TextView textView, ImageView imageView) {
            this.fragment = fragment;
            this.linearLayout = linearLayout;
            this.textView = textView;
            this.imageView = imageView;
        }

        public Tab(Fragment fragment, LinearLayout linearLayout, TextView textView, ImageView imageView,int getFocusedColor,int lostFocusedColor) {
            this.fragment = fragment;
            this.linearLayout = linearLayout;
            this.textView = textView;
            this.imageView = imageView;
            this.getFocusedColor=getFocusedColor;
            this.lostFocusedColor=lostFocusedColor;
        }

        private void getFocused(FragmentManager fragmentManager){
            FragmentTransaction transaction= fragmentManager.beginTransaction();
            transaction.show(fragment);
            transaction.commit();
            switchColor(getFocusedColor);
        }

        private void lostFoucs(FragmentManager fragmentManager){
            FragmentTransaction transaction= fragmentManager.beginTransaction();
            transaction.hide(fragment);
            transaction.commit();
            switchColor (lostFocusedColor);
        }

        private void switchColor(int color) {
            textView.setTextColor(color);
            imageView.getDrawable().setTint(color);
        }
    }

    private class ManagerTab{
        private FragmentManager fragmentManager;
        private int content;

        private List<Tab> tablist;
        private Tab currentTab;

        public ManagerTab(int content) {
            tablist=new ArrayList<Tab>();
            fragmentManager=getSupportFragmentManager();
            this.content=content;
        }

        public ManagerTab(int content,List<Tab> tabList) {
            fragmentManager=getSupportFragmentManager();
            this.tablist=tabList;
            this.content=content;
        }

        public void add(Tab tab){
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.add(content,tab.fragment);
            transaction.commit();
            tablist.add(tab);
        }

        private void initManagerTab() {
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            for(Tab tab:tablist){
                transaction.add(content,tab.fragment);
            }
            transaction.commit();
            hideAllTab();
            showTab(tablist.get(0));
        }

        private void hideAllTab() {
            for(Tab tab:tablist){
                tab.lostFoucs(fragmentManager);
            }
        }

        private void showTab(Tab tab) {
            tab.getFocused(fragmentManager);
            currentTab=tab;
        }

        private void switchTab(Tab tab) {
            currentTab.lostFoucs(fragmentManager);
            tab.getFocused(fragmentManager);
            currentTab=tab;
        }

        public void finalize() {
            currentTab.lostFoucs(fragmentManager);
        }
    }

}