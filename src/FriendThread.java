public class FriendThread extends Thread {
    public Object lock;
    String name="";
    private static final String master="master";
    public FriendThread(Object lock, String name) {
        super();
        this.lock = lock;
        this.name=name;
    }

    @Override
    public void run(){
        try {
            while (true) {
                synchronized (lock) {
                    lock.wait();
                    if (exchange.FriendsThreadsStart) {
                        if (!exchange.introInfo[3].equals(exchange.replyInfo[3])) {
                            synchronized (exchange.address.get(exchange.introInfo[2])) {
                                modifyInfo(exchange.introInfo);
                                exchange.address.get(exchange.introInfo[2]).notify();
                            }
                        }
                    } else {
                        System.out.println("\nProcess " + name + " has received no calls for 1 second, ending...");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void modifyInfo(String[] info){
        exchange.replyInfo[0]=info[2];
        exchange.replyInfo[1]=type.reply.toString();
        exchange.replyInfo[2]=info[0];
        exchange.replyInfo[3]=info[3];
    }
}
