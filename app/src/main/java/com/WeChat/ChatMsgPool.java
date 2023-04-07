package com.WeChat;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * @projectName WeChat
 * @package     com.WeChat
 * @className:  ChatMsgPool
 * @description 运行时 信息缓存池
 * @author      Rebyrd
 * @createDate  2023/04/04
 * @version     v0.10
 */
public class ChatMsgPool {

    public interface Observer{
        public void execute(ChatMsg msg);
    }

    private Map<String, ArrayList<ChatMsg>> msgPool;
    private Comparator<ChatMsg> cmpr_msgTime;
    private static final ChatMsgPool globalPool = new ChatMsgPool();

    private ArrayList<Observer> observers = new ArrayList<>();

    public static ChatMsgPool getGlobalInstance(){
        return globalPool;
    }

    // 采用阻塞队列缓冲待处理消息
    private BlockingDeque<ChatMsg> waiting_processor_queue = new LinkedBlockingDeque<>();

    // 独立线程处理新的消息
    private Thread processor = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                ChatMsg msg = null;
                try {
                    msg = waiting_processor_queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /**
                 * 回调通知数据改变
                 */
                for(Observer ob:observers){
                    ob.execute(msg);
                }
            }
        }
    });

    /**
     * 这里可以替换为SQLite读取
     */
    static {

        // 测试 预设信息
        globalPool.msgPool.put("00000000",new ArrayList<ChatMsg>());
        globalPool.msgPool.put("94676605",new ArrayList<ChatMsg>(){{
            add(new ChatMsg("94676605",Contact.getmUID(),"如果放弃我终生遗憾",new Date(1635143634234l), ChatMsg.Origin.REMOTE));
        }});
        globalPool.msgPool.put("79926561",new ArrayList<ChatMsg>(){{
            add(new ChatMsg("79926561",Contact.getmUID(),"不懂得重视同伴的人，是最最差劲的废物！",new Date(1635143634234l), ChatMsg.Origin.REMOTE));
        }});
        globalPool.msgPool.put("37630877",new ArrayList<ChatMsg>(){{
            add(new ChatMsg("37630877",Contact.getmUID(),"正是因为我们看不见,那才可怕。",new Date(1635143634234l), ChatMsg.Origin.REMOTE));
        }});
        globalPool.msgPool.put("38870952",new ArrayList<ChatMsg>(){{
            add(new ChatMsg("38870952",Contact.getmUID(),"虽然我是影子，但是光越强影就越浓。",new Date(1635143634234l), ChatMsg.Origin.REMOTE));
        }});
        globalPool.msgPool.put("69855056",new ArrayList<ChatMsg>(){{
            add(new ChatMsg("69855056",Contact.getmUID(),"不管是失败者多么厉害，都是赢家更夺人眼球。",new Date(1635143634234l), ChatMsg.Origin.REMOTE));
        }});
        globalPool.msgPool.put("54338014",new ArrayList<ChatMsg>(){{
            add(new ChatMsg("54338014",Contact.getmUID(),"不如说少年漫画对于我这样肤浅的人来说过于高深了。因为少年漫画教给读者的并不是友情、努力、胜利，而是有能力的人才能笑到最后，这样极其残酷的现实。因为有能力所以能交到朋友，因为有能力所以能努力，因为有能力所以能得到胜利。这样绝望的现实，我作为有能力的人都难以忍受啊！",new Date(1635143634234l), ChatMsg.Origin.REMOTE));
        }});
        globalPool.msgPool.put("44738583",new ArrayList<ChatMsg>(){{
            add(new ChatMsg("44738583",Contact.getmUID(),"你,如果是君临球场的王者的话,我会打倒你,成为站在球场上时间最长的人!",new Date(1635143634234l), ChatMsg.Origin.REMOTE));
        }});
        globalPool.msgPool.put("73606017",new ArrayList<ChatMsg>(){{
            add(new ChatMsg("73606017",Contact.getmUID(),"哒噗",new Date(1635143634234l), ChatMsg.Origin.REMOTE));
        }});

    }

    public ChatMsgPool() {
        this.msgPool  = new ConcurrentHashMap<>();
        this.cmpr_msgTime = new Comparator<ChatMsg>() {
            @Override
            public int compare(ChatMsg o1, ChatMsg o2) {
                long t1 = o1.getDate().getTime();
                long t2 = o2.getDate().getTime();
                return t1>t2?1:t1==t2?0:-1;
            }
        };
        processor.start();
    }

    /**
     *
     * @param msg
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean addRecvMsg(ChatMsg msg){
        String s = msg.getUID_sender();
        if(msgPool.get(s) == null)
            return false;
        addMsg(s,msg);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean addSendMsg(ChatMsg msg){
        String r = msg.getUID_recipent();
        if(msgPool.get(r) == null)
            return false;
        addMsg(r,msg);
        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        processor.stop();
        save();
        super.finalize();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addMsg(String UID,ChatMsg msg){
        ArrayList<ChatMsg> msgList = msgPool.get(UID);
        msgList.add(msg);

        Contact.getInfoByUID(UID).put("msg_count",msgList.size());
        msgList.sort(cmpr_msgTime);

        try {
            waiting_processor_queue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean attachObserver(Observer ob){return observers.add(ob);}
    public boolean detachObserver(Observer ob){return observers.remove(ob);}

    public ArrayList<ChatMsg> getSession(String UID){return msgPool.get(UID);}

    // 可以设置为保存到SQLite
    public boolean save() {
        return true;
    }


    public static class ChatMsg {
        enum Origin
        {
            LOCAL,REMOTE;
        }
        enum Status
        {
            ERROR,WAITING,SUCCESSFUL;
        }
//        class UID{
//            final String uid;
//            public UID(String uid) {
//                this.uid = uid;
//            }
//        }

        // 消息发送者ID
        private final String UID_sender;
        // 消息接收者ID
        private final String UID_recipent;
        // 消息内容
        private final String text;
        // 日期
        private final Date date;
        // 消息的方向
        private final Origin origin;

        private Status status;

        /**
         * @param UID_sender 发送者UID
         * @param UID_recipent 接收者UID
         * @param text 消息文本
         * @param date 时间: Date类型
         */
        public ChatMsg(String UID_sender, String UID_recipent, String text, Date date, Origin origin){
            this.date = date;
            this.text = text;
            this.UID_sender = UID_sender;
            this.UID_recipent = UID_recipent;
            this.origin = origin;
        }

        public String getUID_sender() {return UID_sender;}

        public String getUID_recipent() {return UID_recipent;}

        public String getText() {return text;}

        public Date getDate() {return date;}

        public Origin getOrigin() {return origin;}

        public Status getStatus() {return status;}

        public void setStatus(Status status) {this.status = status;}


        private static final SimpleDateFormat split = new SimpleDateFormat("yyyy-MM-dd");
        private static final SimpleDateFormat ymd = new SimpleDateFormat("yyyy年MM月dd日");
        private static final SimpleDateFormat md = new SimpleDateFormat("MM月dd日");
        private static final SimpleDateFormat hm = new SimpleDateFormat("hh:mm");

        public static String formatDate(Date date){
            Date now = new Date();
            String[] n = split.format(now).split("-");
            String[] d = split.format(date).split("-");

            if(n[0].equals(d[0])){
                if(n[1].equals(d[1]) && n[2].equals(d[2]))  return hm.format(date);
                else return md.format(date); // 不是当天
            }else return ymd.format(date); //年份不同
        }

    }
}