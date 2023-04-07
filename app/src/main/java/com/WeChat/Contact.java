package com.WeChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName     WeChat
 * @package         com.WeChat
 * @className:      Contact
 * @description     好友信息，（全局静态公用）
 * @author          Rebyrd
 * @recentModify    2023/04/04
 * @version         v0.10
 */
public final class Contact {

    /**
     * 数据结构：
     *      map{
     *          UID_1:map{
     *              name:"user_name",
     *              ...
     *          },
     *          UID_2:map{...},
     *          UID_3:map{...},
     *          ...
     *      }
     */
    private static Map<String, Map<String, Object>> contact = new ConcurrentHashMap<>();

    // 用户名称
    private static String mName;
    // 用户UID
    private static String mUID;

    /**
     * 初始化
     */
    static{
        mName = "demo_name";
        mUID = "12345";

        contact.put("00000000",new HashMap<String,Object>(){{
            put("name","测试");
            put("header",R.mipmap.kenan);
            put("msg_count",0);
            put("show",false);
        }});
        contact.put("94676605",new HashMap<String,Object>(){{
            put("name","路飞");
            put("header",R.mipmap.lufei);
            put("msg_count",1);
            put("show",true);
        }});
        contact.put("79926561",new HashMap<String,Object>(){{
            put("name","鸣人");
            put("header",R.mipmap.mingren);
            put("msg_count",1);
            put("show",true);
        }});
        contact.put("37630877",new HashMap<String,Object>(){{
            put("name","黑崎一护");
            put("header",R.mipmap.yihu);
            put("msg_count",1);
            put("show",true);
        }});
        contact.put("38870952",new HashMap<String,Object>(){{
            put("name","黑子哲也");
            put("header",R.mipmap.heizi);
            put("msg_count",1);
            put("show",true);
        }});
        contact.put("69855056",new HashMap<String,Object>(){{
            put("name","齐木楠雄");
            put("header",R.mipmap.qimu);
            put("msg_count",1);
            put("show",true);
        }});
        contact.put("54338014",new HashMap<String,Object>(){{
            put("name","黑神目泷");
            put("header",R.mipmap.heishen);
            put("msg_count",1);
            put("show",true);
        }});
        contact.put("44738583",new HashMap<String,Object>(){{
            put("name","日向翔阳");
            put("header",R.mipmap.xiangyang);
            put("msg_count",1);
            put("show",true);
        }});
        contact.put("73606017",new HashMap<String,Object>(){{
            put("name","贝鲁");
            put("header",R.mipmap.beilu);
            put("msg_count",1);
            put("show",true);
        }});

    }

    // 禁止实例化，替换默认 public 构造方法
    private Contact() {
        throw new AssertionError("No " + Contact.class.getName() + " instances for you !");  // 防止反射实例化
    }

    public static Map<String, Object> getInfoByUID(String UID) {
        return contact.get(UID);
    }

    public static ArrayList<String> getUIDByName(String name) {
        Map<String, Object> info = null;
        ArrayList<String> UIDs = new ArrayList<String>();
        for (String UID : contact.keySet()) {
            info = contact.get(UID);
            if (name.equals(info.get("name"))) {
                UIDs.add(UID);
            }
        }
        return UIDs;
    }

    public static boolean contain(String UID){return contact.keySet().contains(UID);}

    public static boolean reName(String UID, String name) {
        Map<String, Object> info = contact.get(UID);
        if (info == null)
            return false;
        else
            info.put("name", name);
        return true;
    }

    public static Set<String> getKeys(){return contact.keySet();}

    public static boolean save() {
        return true;
    }

    public static String getmUID() {return mUID;}

    public static String getmName() {return mName;}
}