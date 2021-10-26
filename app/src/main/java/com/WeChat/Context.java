package com.WeChat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @projectName WeChat
 * @package     com.WeChat
 * @className:  Context
 * @description 聊天界面
 * @author      Rebyrd
 * @createDate  2021/10/25
 * @version     v0.02
 */
public class Context extends AppCompatActivity {

    private TextView name;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context);

        name=findViewById(R.id.txt_top);
        imageView=findViewById(R.id.ret_context);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        name.setText(intent.getStringExtra("name"));
    }
}