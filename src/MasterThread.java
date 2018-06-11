public class MasterThread extends Thread{
    public static String[] introInfo = new String[4];
    public static String[] replyInfo = new String[4];

    public MasterThread() {
        super();
    }

    @Override
    public void run(){
        while (true) {
            synchronized (this) {
                if (introInfo[3] != exchange.introInfo[3]) {
                    for (int i = 0; i < introInfo.length; i++) {
                        introInfo[i] = exchange.introInfo[i];
                    }
                    System.out.println(exchange.introInfo[0] + " received intro message from " + exchange.introInfo[2] + "[" + exchange.introInfo[3] + "]");
                } else if (replyInfo[3] != exchange.replyInfo[3]) {
                    for (int i = 0; i < replyInfo.length; i++) {
                        replyInfo[i] = exchange.replyInfo[i];
                    }
                    System.out.println(exchange.replyInfo[0] + " received reply message from " + exchange.replyInfo[2] + "[" + exchange.replyInfo[3] + "]");
                } else if (!exchange.MasterThreadsStart) {
                    System.out.println("\nMaster has received no replies for 1.5 seconds, ending...");
                    break;
                }
            }
        }
    }
}
