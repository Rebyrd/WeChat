# <center> WeChat </center>

## v0.10
   * [java源码](app/src/main/java/com/WeChat/)
      **界面实现**
   
      + [MainActivity](app/src/main/java/com/WeChat/MainActivity.java) 为主控Activity
    
      + [Fragment_message](app/src/main/java/com/WeChat/Context.java) 为message Tab的Fragment 用于展示消息页面
         - [RecycleAdpater](app/src/main/java/com/WeChat/RecycleAdpater.java) 为message Fragment 的 实现逻辑，内涵 弹框对条目的处理逻辑

      + [Fragment_contact](app/src/main/java/com/WeChat/Fragment_contact.java) 通讯录页面，已实现侧边连快速检索,内置 RecycleView 和其 Adapter (懒得分两个文件写~)，待开发……

      + [Fragment_find](app/src/main/java/com/WeChat/Fragment_config.java) 发现页面，待开发中……

      + [Fragment_config](app/src/main/java/com/WeChat/Fragment_config.java) 设置页面，待开发中……

      + [Context](app/src/main/java/com/WeChat/Context.java) 聊天界面实现 内容通过ListView实现（推荐RecycleView实现，这里作者是想学习一下），
         - [ChatMsgListAdapter](app/src/main/java/com/WeChat/ChatMsgListAdapter.java) 为消息展示逻辑
      
      
      **业务逻辑**
   
      + [ChatMsgPool](app/src/main/java/com/WeChat/ChatMsgPool.java) 消息存储池

      + [Contact](app/src/main/java/com/WeChat/Contact.java) 通讯录列表，这里为测试阶段，直接设置静态信息，可以选择是由SQLite实现。全局静态，禁止实例化

      + [MsgService](app/src/main/java/com/WeChat/MsgService.java) 消息后台服务，用于与后端通信，获取信息

   
   * [resource](app/src/main/res/)
      **资源文件**
      + [layout](app/src/main/res/layout/)
         - [top](app/src/main/res/layout/top.xml) 主界面 title 栏
         - [bottom](app/src/main/res/layout/bottom.xml) 为底部标签控件，实现Tab标签逻辑

         - [fragment_message](app/src/main/res/layout/fragment_message.xml) 消息 界面
         - [fragment_contact](app/src/main/res/layout/fragment_contact.xml) 通讯录 界面
         - [activity_main](app/src/main/res/layout/activity_main.xml) 为主界面
         - [activity_context](app/src/main/res/layout/activity_context.xml) 为聊天界面
         
         - [chat_item_left](app/src/main/res/layout/chat_item_left.xml) 聊天消息 **左气泡** 展示，ListView的item;Type 是Left
         - [chat_item_right](app/src/main/res/layout/chat_item_right.xml) 聊天消息 **右气泡** 展示，ListView的item;Type 是Right

         - [recycle_content_header](app/src/main/res/layout/recycle_contact_header_item.xml) 通讯录页面的 RecycleView的Header视图 (用于索引)
         - [recycle_content_normal](app/src/main/res/layout/recycle_contact_normal_item.xml) 通讯录页面的 RecycleView的 item 视图 （展示好友）
         - [recycle_message](app/src/main/res/layout/recycle_message_item.xml) 消息界面 的 item 视图
         - ……

      + [menu](app/src/main/res/menu/)
         - [popupmenu](app/src/main/res/menu/popupmenu.xml) 为菜单内容
   

### 业务逻辑
   1. Service
      通过后台Service实时与服务器进行信息交换，采用 OkHttp3 像后端 **请求(GET**)、**上传(POST)** 数据，
   
   2. 信息池
      采用 **ChatMsgPool** 全局可见且静态（在启动时加载所有本地信息）来存储信息。其功能作为 Service 和 UI界面 的桥梁，设置 消息Observer(消息回调) 来实现 绑定UI业务。
      消息存储采用 并发Map 实现消息缓存。
      消息处理使用 **阻塞队列** ，防止 OkHttp3 多线程对 UI业务的干扰。


### 演示

![demo.gif](demo.gif)