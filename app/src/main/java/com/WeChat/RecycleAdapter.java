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
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {

    private Context context;
    private int itemID;
    private Map<String, List<Object>> resource;
    private Field mPopupField;
    private float[] toPosition =new float[2];
    private PopMenu popMenu;

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
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent();
            intent.setClass(context, com.WeChat.Context.class);
            intent.putExtra("name",((TextView)view.findViewById(R.id.item_name)).getText());
            context.startActivity(intent);
        }
    };
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

    public RecycleAdapter(Context context, int itemID, Map<String, List<Object>> resource) {
        this.context = context;
        this.itemID=itemID;
        this.resource=resource;
        popMenu =new PopMenu(context);
        try {
            mPopupField = PopupMenu.class.getDeclaredField("mPopup");
            mPopupField.setAccessible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(itemID,parent,false));
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

    public void setTop(int position){
        moveItem(position,0);
    }

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
            imageView=(ImageView) itemView.findViewById(R.id.item_image);
        }
    }

    @SuppressLint("RestrictedApi")
    public class PopMenu extends MenuBuilder {
        private LinearLayout linearLayout=new LinearLayout(context){{
            setOrientation(VERTICAL);
            setBackgroundColor(getResources().getColor(R.color.menuBackground));
        }};

        private View anchorView;
        private boolean isShowing;
        private PopupWindow popupWindow;

        public boolean isShowing() {return isShowing;}
        public int getHeight(){return size()*123;}  // debug 手动找^-^高度，此时view还未渲染，获取不到高度
        public int getWidth(){return linearLayout.getWidth();}
        public void setAnchorView(View anchorView) {this.anchorView = anchorView;}

        public PopMenu(Context context) {
            super(context);
        }

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
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecycleAdapter.this.deleteItem(((RecyclerView)anchorView.getParent()).getChildLayoutPosition(anchorView));
                                popupWindow.dismiss();
                            }
                        });
                        break;
                    case R.id.top_menu:
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                setTop(((RecyclerView)anchorView.getParent()).getChildLayoutPosition(anchorView));
                                popupWindow.dismiss();
                            }
                        });
                        break;
                }
                linearLayout.addView(textView);
            }
            createPopupWindows();
        }

        public void createPopupWindows() {
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

        public void show(int x,int y){
            if(isShowing)return;
            popupWindow.setFocusable(true);
            popupWindow.showAsDropDown(anchorView,x,y);
            isShowing=true;
        }

        public void clear(){
            linearLayout.removeAllViews();
            linearLayout.invalidate();
            for(int i=size()-1;i>=0;i--){
                removeItemAt(i);
            }
        }

        public void dismiss(){
            if(isShowing){
                popupWindow.dismiss();
            }
        }

        public int getItemCount(){
            return size();
        }
    }
}