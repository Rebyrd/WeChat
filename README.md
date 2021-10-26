# <center> WeChat </center>

## v0.01
微信主框体
 * [java源码](app/src/main/java/com/WeChat/)
    + [MainActivity.java](app/src/main/java/com/WeChat/MainActivity.java) 为主控类
 * [resource](app/src/main/res/)
    + [layout](app/src/main/res/layout/)
        - [top.xml](app/src/main/res/layout/top.xml) 为title栏
        - [bottom.xml](app/src/main/res/layout/bottom.xml) 为底部标签控件
        - [activity_main.xml](app/src/main/res/layout/activity_main.xml) 为主界面


## v0.02
message界面实现
 * [java源码](app/src/main/java/com/WeChat/)
    + [Fragment_message.java](app/src/main/java/com/WeChat/Context.java) 为message Tab的Fragment
    + [RecycleAdpater.java](app/src/main/java/com/WeChat/RecycleAdpater.java) 为message Fragment 的 实现逻辑
    + [Context.java](app/src/main/java/com/WeChat/Context.java) 为聊天界面Activity
    + [MainActivity.java](app/src/main/java/com/WeChat/MainActivity.java) 为主控Activity
 * [resource](app/src/main/res/)
    + [layout](app/src/main/res/layout/)
        - [top.xml](app/src/main/res/layout/top.xml) 为title栏
        - [bottom.xml](app/src/main/res/layout/bottom.xml) 为底部标签控件
        - [recyce_item.xml](app/src/main/res/layout/recyce_item.xml) 为item内容
        - [fragment_message.xml](app/src/main/res/layout/fragment_message.xml) 为message内容
        - [activity_main.xml](app/src/main/res/layout/activity_main.xml) 为主界面
        - [activity_context.xml](app/src/main/res/layout/activity_context.xml) 为聊天界面
    + [menu](app/src/main/res/menu/)
        - [popupmenu](app/src/main/res/menu/popupmenu.xml) 为菜单内容