package com.WeChat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

/**
 * @projectName     WeChat
 * @package         com.WeChat
 * @className:      RecycleAdapter
 * @description     recycleView 及 menu 内容逻辑
 * @author          Rebyrd
 * @recentModify    2023/04/04
 * @version         v0.10
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    // item资源文件
    private int itemRes;
    private Map<String, List<Object>> resource;
    // 触控坐标（相对于item控件）
    private float[] toPosition =new float[2];
    // 菜单对象
    private PopMenu popMenu;

    // 长按事件（监听item 实现弹出菜单）
    private View.OnLongClickListener onLongClickListener=new View.OnLongClickListener() {

        @SuppressLint("RestrictedApi")
        @Override
        public boolean onLongClick(View view) {

            popMenu.init(R.menu.popupmenu);
            popMenu.setAnchorView(view);
            int heightParent=((View)view.getParent()).getHeight();
            if(heightParent > (popMenu.getHeight() + view.getTop() + toPosition[1])) {
                popMenu.show((int) toPosition[0], (int) toPosition[1] - view.getHeight());
            }else{
                popMenu.show((int) toPosition[0], (int) toPosition[1] - popMenu.getHeight() - view.getHeight());
            }
            return true;
        }
    };

    // 点击事件 （实现跳转到特定 Activity）
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent();
            intent.setClass(context, com.WeChat.Context.class);
            int index = recyclerView.indexOfChild(view);
            intent.putExtra("UID", (String) resource.get("UID").get(index));
            context.startActivity(intent);
        }
    };
    // 触控事件 （获取实时触控坐标）
    private View.OnTouchListener onTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                toPosition[0]=event.getX();
                toPosition[1]=event.getY();
            }
            return false;
        }
    };

    /**
     * @param context context资源
     * @param itemRes item资源文件
     * @param resource item内容
     */
    public RecycleAdapter(Context context,RecyclerView recyclerView, int itemRes, Map<String, List<Object>> resource) {
        this.context = context;
        this.itemRes = itemRes;
        this.resource=resource;
        this.recyclerView = recyclerView;
        popMenu =new PopMenu(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(itemRes,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (resource.get("name").size() > position) {
            String name     =(String)   resource.get("name").get(position);
            String context  =(String)   resource.get("context").get(position);
            int imageID     =(int)      resource.get("image").get(position);
            String time     =(String)   resource.get("time").get(position);

            if (null!=name)     holder.name.setText(name);
            if (null!=context)  holder.context.setText(context);
            if (0!=imageID)     holder.imageView.setImageResource(imageID);
            if (null!=time)     holder.time.setText(time);
        }
        holder.itemView.setOnLongClickListener(onLongClickListener);
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setOnTouchListener(onTouchListener);
    }

    @Override
    public int getItemCount() {
        return resource.get("context")==null ? 0:resource.get("context").size();
    }

    /**
     * 添加新的item
     * @param name 名称
     * @param context 内容
     * @param imageID 头像
     * @param time 时间
     * @param position 插入的目标位置 (-1代表最后)
     */
    public void insertItem(String name,String context,int imageID,String time,int position){
        if(position==-1||position>=resource.get("name").size()){
            resource.get("name").add(name);
            resource.get("context").add(context);
            resource.get("image").add(imageID);
            resource.get("time").add(time);
            notifyItemInserted(resource.get("name").size()-1);
        }else{
            resource.get("name").add(position,name);
            resource.get("context").add(position,context);
            resource.get("image").add(position,imageID);
            resource.get("time").add(position,time);
            notifyItemInserted(position);
        }
    }

    /**
     * 置顶item
     * @param position item位置
     */
    public void setTop(int position){
        moveItem(position,0);
    }

    /**
     * 将item移动到新的位置 （位置间item向后平移）
     * @param fromPosition 旧位置
     * @param toPosition 新位置
     */
    public void moveItem(int fromPosition,int toPosition) {
        if(fromPosition < resource.get("name").size()&&toPosition < resource.get("name").size()){
            resource.get("name").add(toPosition,resource.get("name").get(fromPosition));
            resource.get("context").add(toPosition,resource.get("context").get(fromPosition));
            resource.get("image").add(toPosition,resource.get("image").get(fromPosition));
            resource.get("time").add(toPosition,resource.get("time").get(fromPosition));
            fromPosition++;
            resource.get("name").remove(fromPosition);
            resource.get("context").remove(fromPosition);
            resource.get("image").remove(fromPosition);
            resource.get("time").remove(fromPosition);
            notifyItemMoved(fromPosition -1,toPosition);
        }
    }

    /**
     * 删除item
     * @param position 删除指定位置的item
     */
    public void deleteItem(int position){
        if(position < resource.get("name").size()){
            resource.get("name").remove(position);
            resource.get("context").remove(position);
            resource.get("image").remove(position);
            resource.get("time").remove(position);
            notifyItemRemoved(position);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView context;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            context = (TextView) itemView.findViewById(R.id.item_context);
            time = (TextView) itemView.findViewById(R.id.item_time);
            imageView=(ImageView) itemView.findViewById(R.id.item_image_message);
        }
    }

    @SuppressLint("RestrictedApi")
    public class PopMenu extends MenuBuilder {
        private View anchorView;
        private boolean isShowing;
        private PopupWindow popupWindow;
        private LinearLayout linearLayout=new LinearLayout(context){{
            setOrientation(VERTICAL);
            setBackgroundColor(getResources().getColor(R.color.menuBackground));
        }};

        public boolean isShowing() {return isShowing;}
        public int getHeight(){return size()*123;}  // debug 手动找~-~高度，此时view还未渲染，获取不到高度
        public int getWidth(){return linearLayout.getWidth();}
        public void setAnchorView(View anchorView) {this.anchorView = anchorView;}

        public PopMenu(Context context) {
            super(context);
        }

        /**
         * 初始化菜单
         * @param menuRes menu资源文件
         */
        public void init(int menuRes){
            new MenuInflater(context).inflate(menuRes,this);
            for(MenuItem menuItem:getVisibleItems()) {
                TextView textView=new AppCompatTextView(context){{
                    setText(menuItem.getTitle());
                    setPadding(80,30,100,30);
                    setTextSize(18);
                }};
                switch (menuItem.getItemId()){
                    case R.id.del_menu:
                        // 监听删除按钮
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = ((RecyclerView)anchorView.getParent()).getChildLayoutPosition(anchorView);
                                Contact.getInfoByUID((String)resource.get("UID").get(index)).put("show",false);
                                RecycleAdapter.this.deleteItem(index);
                                resource.get("UID").remove(index);
                                popupWindow.dismiss();
                            }
                        });
                        break;
                    case R.id.top_menu:
                        // 监听置顶按钮
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = ((RecyclerView)anchorView.getParent()).getChildLayoutPosition(anchorView);
                                setTop(index);

                                List<Object> UIDs = resource.get("UID");
                                UIDs.add(0,UIDs.get(index));
                                UIDs.remove(index+1);

                                popupWindow.dismiss();
                            }
                        });
                        break;
                }
                linearLayout.addView(textView);
            }
            createPopupWindows();
        }

        private void createPopupWindows() {
            popupWindow = new PopupWindow(linearLayout);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    clear();
                    isShowing=false;
                }
            });
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setElevation(10);
        }

        /**
         * 显示菜单
         * @param x x坐标（相对锚点view左下x坐标的偏移）
         * @param y y坐标（相对锚点view左下y坐标的偏移）
         */
        public void show(int x,int y){
            if(isShowing)return;
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown(anchorView,x,y);
            isShowing=true;
        }

        /**
         * 清空内容
         */
        public void clear(){
            linearLayout.removeAllViews();
            linearLayout.invalidate();
            for(int i=size()-1;i>=0;i--){
                removeItemAt(i);
            }
        }

        /**
         * 关闭菜单
         */
        public void dismiss(){
            if(isShowing){
                popupWindow.dismiss();
            }
        }

        /**
         * @return item数量
         */
        public int getItemCount(){
            return size();
        }
    }
}