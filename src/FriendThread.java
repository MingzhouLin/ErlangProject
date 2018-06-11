public class FriendThread extends Thread {
    public Object lock;
    public Object replylocker;
    String name = "";
    //0:callee 1:type 2:caller 3:time
    public String[] introInfo = {"","","",""};
    private String introUpdatedTime = "";
    public String[] replyInfo ={"","","",""};
    private String replyUpdatedTime = "";
    private static final String master = "master";

    public FriendThread(Object lock, String name, Object replylocker) {
        super();
        this.lock = lock;
        this.replylocker = replylocker;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (lock) {
                    lock.wait();
//                    System.out.println(name);
                    if (exchange.FriendsThreadsStart) {
                        if (!introUpdatedTime.equals(introInfo[3])) {
                            introUpdatedTime = introInfo[3];
                            FriendThread callee = exchange.address.get(introInfo[2]);
                            synchronized (exchange.master) {
                                exchange.master.thread = this;
                                exchange.master.type = Type.intro;
                                exchange.master.notify();
                            }
                            synchronized (callee.lock) {
                                modifyInfo(this.introInfo, callee);
                                callee.lock.notify();
                            }
                        }
                        if (!replyUpdatedTime.equals(replyInfo[3])) {
//                            System.out.println(replyUpdatedTime+" "+replyInfo[3]);
                            replyUpdatedTime=replyInfo[3];
                            synchronized (exchange.master) {
                                exchange.master.thread = this;
                                exchange.master.type = Type.reply;
                                exchange.master.notify();
                            }
                        }
                    } else {
                        System.out.println("\nProcess " + name + " has received no calls for 1 second, ending...");
                    }
                }
//                synchronized (replylocker){
//                    replylocker.wait();
//                    replyUpdatedTime=replyInfo[3];
//                    synchronized (exchange.master){
//                        exchange.master.thread=this;
//                        exchange.master.type=Type.reply;
//                        exchange.master.notify();
//                    }
//                }

            }
        } catch (
                InterruptedException e)

        {
            e.printStackTrace();
        }

    }

    private void modifyInfo(String[] info, FriendThread callee) {
        callee.replyInfo[0] = info[2];
        callee.replyInfo[1] = Type.reply.toString();
        callee.replyInfo[2] = info[0];
        callee.replyInfo[3] = info[3];
    }
}
