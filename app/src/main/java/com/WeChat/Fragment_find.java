package com.WeChat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * @projectName     WeChat
 * @package         com.WeChat
 * @className:      Fragment_find
 * @description     发现界面
 * @author          Rebyrd
 * @recentModify    2021/10/25
 * @version         v0.02
 */
public class Fragment_find extends Fragment {

    public Fragment_find() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find, container, false);
    }
}
