package edu.bupt.beibei.util;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Stoper {

    private static final String TAP_APP = "adb -s %s shell input tap 126 178";

    private static final String OPEN_MOBILE = "adb -s %s shell input keyevent 3";

    private static final String UNLOCK = "adb -s %s shell input swipe 500 1900 500 1400 200";

    private static final String SET_FLAG = "adb -s %s push D:\\codes\\game_admin\\src\\main\\resources\\run /sdcard/tmp";

    private static final String STOP_1 = "adb -s %s shell am force-stop com.meizu.pays";
    private static final String STOP_2 = "adb -s %s shell am force-stop com.samsung.android.scloud";
    private static final String STOP_3 = "adb -s %s shell am force-stop com.netease.xyq";

    private static final String OPEN_TCP = "adb -s %s tcpip 5555";


    private static final String[] devices = new String[]{"721QECRHCABZQ", "721QSDLB996HR", "721CECRE22M32", "721QEBRA2637P", "92c9a8cb", "5575ca7b", "6bd8c8b3", "721CECSM272YC", "721CECRE22K32", "721QSDMVIA6HR", "721QSDYIEO6HR", "721QEBR925PTT", "721QSDORVR6HR", "721QSDDN7Q6HR", "721QACRG62MXL", "c948deb6", "721QECRK2TE4W", "721QEDRJ2R99P", "5dfcc812", "17fea877", "721MECRC23LRE", "721QACRP35GKM", "721QECRR35WKL", "721QECRHJZYQZ", "721QECRM2ZN3F", "721CECRJ24QW7", "721QECSC3A6VM"};
    private static final String[] devices1 = new String[]{"92c9a8cb"};
    private static LinkedList<String> taskDevices = new LinkedList();

    static {
        for (String s : devices) {
            taskDevices.add(s);
        }
        System.out.println(taskDevices.size());
    }

    public static void main(String[] args) throws Exception {
        sleep(1);
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < devices.length; i++) {
            pool.submit(new Job());
            sleepMillions(200);
        }
        pool.shutdown();
    }

    static class Job implements Runnable {
        @Override
        public void run() {
            try {
                String deviceId = taskDevices.pop();
                System.out.println(deviceId);
//                exec(OPEN_MOBILE, deviceId);
//                sleep(4);
//                exec(UNLOCK, deviceId);
//                sleep(3);
//                exec(OPEN_TCP, deviceId);
//                sleep(3);
//                exec(OPEN_MOBILE, deviceId);
//                sleep(3);
//                exec(OPEN_MOBILE, deviceId);
//                sleep(4);
//                exec(SET_FLAG, deviceId);
                exec(STOP_1, deviceId);
                sleep(2);
                exec(STOP_2, deviceId);
                sleep(2);
                exec(STOP_3, deviceId);
                sleep(2);
//                exec(TAP_APP, deviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void exec(String command, String deviceId) throws Exception {
        Runtime.getRuntime().exec(String.format(command, deviceId));
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sleepMillions(int millions) {
        try {
            Thread.sleep(millions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
