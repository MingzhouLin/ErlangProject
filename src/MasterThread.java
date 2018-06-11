public class MasterThread extends Thread {
    public FriendThread thread;
    public Type type;

    public MasterThread() {
        super();
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    this.wait();
//                    System.out.println("start");
                    if (exchange.MasterThreadsStart) {
                        if (type.equals(Type.intro)) {
                            System.out.println(thread.introInfo[0] + " received intro message from " + thread.introInfo[2] + "[" + thread.introInfo[3] + "]");
                        }else if (type.equals(Type.reply)){
                            System.out.println(thread.replyInfo[0] + " received reply message from " + thread.replyInfo[2] + "[" + thread.replyInfo[3] + "]");
                        }
                    } else {
                        System.out.println("\nMaster has received no replies for 1.5 seconds, ending...");
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
