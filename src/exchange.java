import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class exchange {
    public static boolean FriendsThreadsStart = true;
    public static boolean MasterThreadsStart = true;
    public static MasterThread master;
    public static HashMap<String, FriendThread> address = new HashMap<>();

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
        System.out.println();
        //start master process
        Object masterlock = new Object();
        master = new MasterThread();
        master.start();
//        address.put("master", masterlock);

        //start process for each person.
        for (String caller :
                map.keySet()) {
            Object lock = new Object();
            Object replyLocker = new Object();
            FriendThread thread = new FriendThread(lock, caller, replyLocker);
            thread.start();
            address.put(caller, thread);
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //send message
        for (Map.Entry<String, String[]> entry :
                map.entrySet()) {
            try {
                for (String s :
                        entry.getValue()) {
                    FriendThread callee = address.get(s);
                    synchronized (callee.lock) {
                        long millisecond = System.currentTimeMillis();
                        String milli = String.valueOf(millisecond);
                        callee.introInfo[0] = s;
                        callee.introInfo[1] = Type.intro.toString();
                        callee.introInfo[2] = entry.getKey();
                        callee.introInfo[3] = milli.substring(milli.length() - 6, milli.length());
                        callee.lock.notify();
                    }
                    Thread.sleep(100);
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
                synchronized (address.get(caller).lock) {
                    address.get(caller).lock.notify();
                }
            }

            Thread.sleep(500);
            MasterThreadsStart = false;
            synchronized (master) {
                master.notify();
            }
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

enum Type {
    intro, reply;
}

