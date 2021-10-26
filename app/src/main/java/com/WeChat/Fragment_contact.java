package com.WeChat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * @projectName WeChat
 * @package     com.WeChat
 * @className:  Fragment_concat
 * @description 通讯录界面
 * @author      Rebyrd
 * @createDate  2021/10/25
 * @version     v0.02
 */
public class Fragment_contact extends Fragment {

    public Fragment_contact() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentc
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }
}
