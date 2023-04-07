package com.WeChat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.promeg.pinyinhelper.Pinyin;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @projectName     WeChat
 * @package         com.WeChat
 * @className:      Fragment_concat
 * @description     通讯录界面
 * @author          Rebyrd
 * @recentModify    2021/10/25
 * @version         v0.02
 */
public class Fragment_contact extends Fragment implements View.OnClickListener,View.OnFocusChangeListener{

    private static final int HEADER_ITEM = 1;
    private static final int NORMAL_ITEM = 2;

    private RecyclerView recyclerView;
    private RecycleAdapter recycleAdapter;
    private View view;
    private int selected = R.id.index_A;
    private ArrayList<View> sideItemList = new ArrayList<>();

    // recyclerView的item内容
    ArrayList<Map<String,Object>> resource;
    private LinearLayoutManager layoutManager;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    public RecycleAdapter getRecycleAdapter() {
        return recycleAdapter;
    }
    public ArrayList<Map<String,Object>> getResource() {
        return resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Fragment_contact() {
        // Required empty public constructor
        initItem();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initItem() {
        resource = new ArrayList<Map<String,Object>>(){{
            for(char i='A';i<='Z';i++) {
                char finalI = i;
                add(new HashMap<String, Object>() {{
                    put("tag", HEADER_ITEM);
                    put("name", String.valueOf(finalI));
                    put("image", null);
                }});
            }
        }};

        for(String UID : Contact.getKeys()) {
            Map<String,Object> info = Contact.getInfoByUID(UID);
            resource.add(new HashMap<String, Object>() {{
                put("tag", NORMAL_ITEM);
                put("name", info.get("name"));
                put("image", info.get("header"));
            }});
        }

        resource.sort(new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // 根据英文排序
                Comparator<Object> compare = Collator.getInstance(java.util.Locale.US);
                // 转化为大写拼音
                String c1 = Pinyin.toPinyin((String)o1.get("name"),"");
                String c2 = Pinyin.toPinyin((String)o2.get("name"),"");
                // 升序
                return compare.compare(c1,c2);
            }
        });

        boolean lastIsHeader = true;
        for(int i=resource.size()-1;i>=0;i--){
            if((Integer) resource.get(i).get("tag") == HEADER_ITEM){
                if(lastIsHeader) resource.remove(i);
                else lastIsHeader = true;
            }else lastIsHeader = false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentc

        view = inflater.inflate(R.layout.fragment_contact, container, false);

        recycleAdapter=new Fragment_contact.RecycleAdapter(view.getContext(),resource);

        recyclerView = view.findViewById(R.id.recyclerView_contact);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(recycleAdapter);

        layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = layoutManager.findFirstVisibleItemPosition();
                if(((int) resource.get(position).get("tag")) == HEADER_ITEM ){

                }
            }
        });

        sideItemList.add(view.findViewById(R.id.index_A));
        sideItemList.add(view.findViewById(R.id.index_B));
        sideItemList.add(view.findViewById(R.id.index_C));
        sideItemList.add(view.findViewById(R.id.index_D));
        sideItemList.add(view.findViewById(R.id.index_E));
        sideItemList.add(view.findViewById(R.id.index_F));
        sideItemList.add(view.findViewById(R.id.index_G));
        sideItemList.add(view.findViewById(R.id.index_H));
        sideItemList.add(view.findViewById(R.id.index_I));
        sideItemList.add(view.findViewById(R.id.index_J));
        sideItemList.add(view.findViewById(R.id.index_K));
        sideItemList.add(view.findViewById(R.id.index_L));
        sideItemList.add(view.findViewById(R.id.index_M));
        sideItemList.add(view.findViewById(R.id.index_N));
        sideItemList.add(view.findViewById(R.id.index_O));
        sideItemList.add(view.findViewById(R.id.index_P));
        sideItemList.add(view.findViewById(R.id.index_Q));
        sideItemList.add(view.findViewById(R.id.index_R));
        sideItemList.add(view.findViewById(R.id.index_S));
        sideItemList.add(view.findViewById(R.id.index_T));
        sideItemList.add(view.findViewById(R.id.index_U));
        sideItemList.add(view.findViewById(R.id.index_V));
        sideItemList.add(view.findViewById(R.id.index_W));
        sideItemList.add(view.findViewById(R.id.index_X));
        sideItemList.add(view.findViewById(R.id.index_Y));
        sideItemList.add(view.findViewById(R.id.index_Z));


        for(Map<String,Object> i:resource) {
            if ((Integer) i.get("tag") == HEADER_ITEM)
                sideItemList.get(((String) i.get("name")).charAt(0) - 'A').setOnClickListener(this);
        }

        return view;
    }


    @Override
    public void onClick(View v) {
        ((GradientDrawable) view.findViewById(selected).getBackground()).setColor(getResources().getColor(R.color.sideUnselected));
        ((GradientDrawable) v.getBackground()).setColor(getResources().getColor(R.color.sideSelected));

        int index = sideItemList.indexOf(v);

        int max = resource.size();
        recyclerView.scrollToPosition(max - 1);
        for (int i = 0; i < max; i++) {
            Map<String,Object> m = resource.get(i);
            if((Integer) m.get("tag") == HEADER_ITEM && ((String)m.get("name")).charAt(0) == (index +'A')){
                layoutManager.scrollToPositionWithOffset(i,0);
                break;
            }
        }

        selected=v.getId();
    }

    private void tryChangeFocusIndex(int id) {
        ((GradientDrawable) view.findViewById(selected).getBackground()).setColor(getResources().getColor(R.color.sideUnselected));
        ((GradientDrawable) view.findViewById(selected).getBackground()).setColor(getResources().getColor(R.color.sideSelected));
        selected=id;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    private static class RecycleAdapter extends RecyclerView.Adapter<Fragment_contact.RecycleAdapter.MyViewHolder>{

        private final Context context;
        private final ArrayList<Map<String,Object>> resource;

        public RecycleAdapter(Context context, ArrayList<Map<String,Object>> resource) {
            this.context = context;
            this.resource=resource;
        }

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=null;
            switch (viewType){
                case HEADER_ITEM:
                    return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycle_contact_header_item,parent,false));
                case NORMAL_ITEM:
                    return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycle_contact_normal_item,parent,false));
                default:
                    return null;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return (int)resource.get(position).get("tag");
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            int viewType = getItemViewType(position);

            switch (viewType){
                case HEADER_ITEM:
                    holder.header.setText((String) resource.get(position).get("name"));
                    break;
                case NORMAL_ITEM:
                    holder.name.setText((String) resource.get(position).get("name"));
                    holder.imageView.setImageResource((Integer) resource.get(position).get("image"));
                    break;
                default:
                    return;
            }
        }

        @Override
        public int getItemCount() {
            return resource.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView header=null;
            TextView name=null;
            ImageView imageView=null;

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @SuppressLint("ResourceType")
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                if (itemView.getSourceLayoutResId() == R.layout.recycle_contact_header_item) {
                    header = (TextView) itemView.findViewById(R.id.item_txt_header);
                } else if (itemView.getSourceLayoutResId() == R.layout.recycle_contact_normal_item) {
                    name = (TextView) itemView.findViewById(R.id.item_name_contact);
                    imageView = (ImageView) itemView.findViewById(R.id.item_image_contact);
                    itemView.setOnClickListener(clickListener);
                }
            }

        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"未开发...",Toast.LENGTH_SHORT).show();
            }
        };

    }
}
