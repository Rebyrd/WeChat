package com.WeChat;

import static com.WeChat.ChatMsgPool.ChatMsg.formatDate;
import static com.WeChat.ChatMsgPool.getGlobalInstance;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.WeChat.ChatMsgPool.ChatMsg;

import java.util.ArrayList;

/**
 * @projectName     WeChat
 * @package         com.WeChat
 * @className:      ChatMsgListAdapter
 * @description     聊天界面的数据适配器（ListView）
 * @author          Rebyrd
 * @recentModify    2023/04/04
 * @version         v0.10
 */
public class ChatMsgListAdapter extends BaseAdapter {

    private String UID;
    private LayoutInflater mInflater;
    private ArrayList<ChatMsg> sessionMsg;

    public ChatMsgListAdapter(Context context, String UID) {
        this.UID = UID;
        mInflater = LayoutInflater.from(context);
        sessionMsg = getGlobalInstance().getSession(UID);
    }

    //获取ListView的项个数
    public int getCount() {
        return sessionMsg.size();
    }

    //获取项
    public Object getItem(int position) {
        return sessionMsg.get(position);
    }

    //获取项的ID
    public long getItemId(int position) {
        return position;
    }

    //获取项的类型
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        ChatMsg msg = sessionMsg.get(position);

        switch (msg.getOrigin()){
            case LOCAL:
                return ChatMsg.Origin.LOCAL.ordinal();
            case REMOTE:
                return ChatMsg.Origin.REMOTE.ordinal();
            default:
                return -1;
        }
    }

    //获取项的类型数
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return ChatMsg.Origin.values().length;
    }

    //获取View
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMsg msg = sessionMsg.get(position);

        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            switch (msg.getOrigin()){
                case LOCAL:
                    //如果是自己发出的消息，则显示的是右气泡
                    convertView = mInflater.inflate(R.layout.chat_item_right, null);
                    break;
                case REMOTE:
                    //如果是对方发来的消息，则显示的是左气泡
                    convertView = mInflater.inflate(R.layout.chat_item_left, null);
                    break;
            }

            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.tvUserHeader = (ImageView) convertView.findViewById(R.id.tv_userhead);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.position = position; // 更新位置，防止底层优化导致位置变更

        if( msg.getOrigin() != ChatMsg.Origin.LOCAL ) {
            viewHolder.tvUserName.setText((String) Contact.getInfoByUID(UID).get("name"));
            viewHolder.tvUserHeader.setImageResource((Integer) Contact.getInfoByUID(UID).get("header"));
        }else {
            viewHolder.tvWaiting = (ImageView) convertView.findViewById(R.id.waiting);

            // 清空动画
            if(viewHolder.objectAnimator != null){
                viewHolder.objectAnimator.end();
                viewHolder.objectAnimator=null;
            }
            switch (msg.getStatus()){
                case ERROR:
                    viewHolder.tvWaiting.setImageResource(R.drawable.ic_error);
                    viewHolder.tvWaiting.setVisibility(View.VISIBLE);
                    break;
                case WAITING:
                    viewHolder.tvWaiting.setImageResource(R.drawable.ic_loding);
                    viewHolder.startWaitingAnim();
                    break;
                case SUCCESSFUL:
                    viewHolder.tvWaiting.setVisibility(View.GONE);
                    break;
            }
        }
        viewHolder.tvSendTime.setText(formatDate(msg.getDate()));
        viewHolder.tvSendTime.setVisibility(View.GONE);

        viewHolder.tvContent.setText(msg.getText());

        return convertView;
    }


    //通过ViewHolder显示项的内容
    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public ImageView tvUserHeader;
        public ImageView tvWaiting;
        public ObjectAnimator objectAnimator = null;

        public int position;

        // 开启等待动画
        public void startWaitingAnim() {
            if(objectAnimator == null) {
                tvWaiting.setVisibility(View.VISIBLE);
                objectAnimator = ObjectAnimator.ofFloat(tvWaiting, "rotation", 0.0f, 360.0f);
                //设置动画时间
                objectAnimator.setDuration(2000);
                //设置动画重复次数，这里-1代表无限
                objectAnimator.setRepeatCount(Animation.INFINITE);
                //设置动画循环模式。
                objectAnimator.setRepeatMode(ValueAnimator.RESTART);
                objectAnimator.start();
            }
        }

        // 关闭等待信号
        public void stopWaitingAnim(boolean isError) {
            if(objectAnimator != null) {
                objectAnimator.end();
                objectAnimator = null;
                if (isError)
                    tvWaiting.setImageResource(R.drawable.ic_error);
                else
                    tvWaiting.setVisibility(View.GONE);
            }
        }

        @Override
        protected void finalize() throws Throwable {
            if(objectAnimator != null)
                objectAnimator.cancel();
            super.finalize();
        }
    }
}
