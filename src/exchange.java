import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class exchange {
    public static boolean FriendsThreadsStart = true;
    public static boolean MasterThreadsStart = true;
    //0:callee 1:type 2:caller 3:time
    public static String[] introInfo = new String[4];
    public static HashMap<String, Object> address = new HashMap<>();
    public static String[] replyInfo = new String[4];

    public static void main(String[] args) {
        new exchange().run();
    }

    public void run() {
        //readfile
        HashMap<String, String[]> map = readFile();
        System.out.println("** Calls to be made **");
        //print
        for (Map.Entry<String, String[]> entry :
                map.entrySet()) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (String callee :
                    entry.getValue()) {
                builder.append(callee + ", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            builder.append("]");
            System.out.println(entry.getKey() + ": " + builder.toString());
        }
        //start master process
        Object masterlock = new Object();
        MasterThread master = new MasterThread();
        master.start();
//        address.put("master", masterlock);

        //start process for each person.
        for (String caller :
                map.keySet()) {
            Object lock = new Object();
            FriendThread thread = new FriendThread(lock, caller);
            thread.start();
            address.put(caller, lock);
        }

        //send message
        for (Map.Entry<String, String[]> entry :
                map.entrySet()) {
            try {
                introInfo[2] = entry.getKey();
                introInfo[1] = type.intro.toString();
                for (String callee :
                        entry.getValue()) {
                    introInfo[0] = callee;
                    long millisecond = System.currentTimeMillis();
                    exchange.introInfo[3] = String.valueOf(millisecond);
                    synchronized (address.get(callee)) {
                        address.get(callee).notify();
                    }
                    int time = (int) (Math.random() * 100);
                    Thread.sleep(time);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //stop process
        try {
            Thread.sleep(1000);
            FriendsThreadsStart = false;
            for (String caller :
                    map.keySet()) {
                synchronized (address.get(caller)) {
                    address.get(caller).notify();
                }
            }

            Thread.sleep(500);
            MasterThreadsStart = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, String[]> readFile() {
        HashMap<String, String[]> map = new HashMap<>();
        try {
            File file = new File("calls.txt");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            while (line != null) {
                String pattern = "(\\{)(\\w+)(\\,\\s)(\\[)(\\S+)(\\]\\}\\.$)";
//                String pattern= "(\\{)(.+)(\\}.$)";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(line);
                String caller = "";
                String callee = "";
                if (m.find()) {
                    caller = m.group(2);
                    callee = m.group(5);
                }
                map.put(caller, callee.split(","));
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}

enum type {
    intro, reply;
}

