package edu.bupt.beibei.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Launcher {

    private static final String GET_PARAM_FORMAT = "%s=%s&";

    private static final SimpleDateFormat DateTimePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String TAP_APP = "adb -s %s shell input tap 126 178";

    private static final String OPEN_MOBILE = "adb -s %s shell input keyevent 3";

    private static final String UNLOCK = "adb -s %s shell input swipe 500 1900 500 1400 200";

    private static final String SET_FLAG = "adb -s %s push D:\\codes\\jdk\\run /sdcard/tmp";

    private static final String STOP_1 = "adb -s %s shell am force-stop com.meizu.pays";
    private static final String STOP_2 = "adb -s %s shell am force-stop com.samsung.android.scloud";
    private static final String STOP_3 = "adb -s %s shell am force-stop com.netease.xyq";

    private static final String OPEN_TCP = "adb -s %s tcpip 5555";

    private static final String URL = "http://121.229.0.127:8088/";
//    private static final String URL = "http://127.0.0.1:8088/";

    private static final String INSTALL_APP = "adb -s %s install -r C:\\Users\\Administrator\\Desktop\\app-release.apk";

    private static Map<String, Long> taskHistoryMap = new HashMap<>();

    private static final Long LOOP_MILLISECONDS = 10 * 60 * 1000L;


    /**
     * @param args owner taskTpe taskParam...
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        String owner = args[0];
        String taskType = args[1];
        if ("install".equals(taskType)) {
            System.out.println("install start");
            String deviceType = args[2];
            install(owner, deviceType);
            return;
        }
        if ("task".equals(taskType)) {
            task(owner);
        }
        if ("device".equals(taskType)) {
            getOutMobile(owner);
        }
    }

    private static void task(String owner) {
        for (int i = 0; i < 1000; i++) {
            Map<Integer, List<String>> taskMap = getTask(owner);
            sendCommand(taskMap);
            System.out.println("sleep for next loop");
            sleep(1200);
        }
    }

    private static void install(String owner, String deviceType) {
        List<String> devices = getDevices(owner);
        System.out.println("install "+devices.size());
        for (String device : devices) {
            if (device == null) {
                continue;
            }
            if ("meizu".equals(deviceType)) {
                if (device.length() > 10) {
                    System.out.println("install  "+device);
                    exec(INSTALL_APP, device);
                }
            } else if ("sanxing".equals(deviceType)) {
                if (device.length() <= 10) {
                    System.out.println("install  "+device);
                    exec(INSTALL_APP, device);
                }
            }
        }
        System.out.println("install end");
    }

    public static void getOutMobile(String owner) {
        try {
            Process process = Runtime.getRuntime().exec("adb devices");

            process.waitFor();
            InputStream inputStream = process.getInputStream();
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String str = new String(bytes);
            String[] ss = str.split("\r\n");
            Set<String> devices = new HashSet<>();
            for (String s : ss) {
                if (s.contains("\t")) {
                    devices.add(s.split("\t")[0]);
                }
            }
            Map<String, String> param = new HashMap<>();
            param.put("owner", owner);
            String rsp = get(URL + "mobile/get", param);
            JSONArray jsonArray = JSON.parseArray(rsp);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String device = jsonObject.getString("device");
                if (device == null) {
                    continue;
                }
                if (!devices.contains(device)) {
                    System.out.println(jsonObject.getIntValue("number") + " " + device);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getDevices(String owner) {
        Map<String, String> param = new HashMap<>();
        param.put("owner", owner);
        String rsp = get(URL + "mobile/get", param);
        JSONArray jsonArray = JSON.parseArray(rsp);
        List<String> devices = new LinkedList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            devices.add(jsonObject.getString("device"));
        }
        return devices;
    }

    private static Map<String, Integer> getTaskMobile(String owner) {
        Map<String, String> param = new HashMap<>();
        param.put("owner", owner);
        String rsp = get(URL + "mobile/getTaskMobile", param);
        JSONArray jsonArray = JSON.parseArray(rsp);
        HashMap<String, Integer> devices = new HashMap<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            devices.put(jsonObject.getString("device"), jsonObject.getIntValue("net"));
        }
        return devices;
    }

    private static Map<Integer, List<String>> getTask(String owner) {
        JSONArray taskStat = getTaskStat(owner);
        Map<String, Integer> taskDevices = getTaskMobile(owner);
        Map<Integer, List<String>> taskMobileMap = new HashMap<>();
        int done = 0;
        int doing = 0;
        int todo = 0;
        for (int i = 0; i < taskStat.size(); i++) {
            JSONObject jsonObject = taskStat.getJSONObject(i);
            int state = jsonObject.getIntValue("state");
            String device = jsonObject.getString("device");
            if (device == null) {
                continue;
            }
            if (state == 2) {
                if (taskDevices.containsKey(device)) {
                    todo++;
                    int mobileNum = jsonObject.getIntValue("mobile_num");
                    int net = taskDevices.get(device);
                    if (taskMobileMap.containsKey(net)) {
                        taskMobileMap.get(net).add(mobileNum + "_" + device);
                    } else {
                        List<String> devices = new LinkedList<>();
                        devices.add(mobileNum + "_" + device);
                        taskMobileMap.put(net, devices);
                    }
                    System.out.println("mobile " + mobileNum + " need restart");
                }
            } else if (state == 0) {
                done++;
            } else {
                doing++;
            }
        }
        System.out.println(getTime() + " watch dog start work, done:" + done + ",doing:" + doing + ", need start:" + todo);
        return taskMobileMap;
    }

    public static void sendCommand(Map<Integer, List<String>> taskMobileMap) {
        if (taskMobileMap.size() == 0) {
            return;
        }
        List<ExecutorService> pools = new LinkedList<>();
        for (Integer key : taskMobileMap.keySet()) {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            List<String> mobiles = taskMobileMap.get(key);
            for (String mobile : mobiles) {
                String[] str = mobile.split("_");
                String number = str[0];
                String device = str[1];
                cleanMobile(device);
                if (!taskHistoryMap.containsKey(device) || System.currentTimeMillis() - taskHistoryMap.get(device) > LOOP_MILLISECONDS) {
                    pool.submit(new StartJob(device, number));
                }
            }
            pools.add(pool);
        }
        taskMobileMap.clear();
        for (ExecutorService pool : pools) {
            pool.shutdown();
        }
    }

    private static void cleanMobile(String device) {
        exec(OPEN_MOBILE, device);
        sleepMilliSeconds(30);
        if (device != null && device.length() > 10) {
            exec(STOP_1, device);
        } else {
            exec(STOP_2, device);
        }
        sleepMilliSeconds(30);
    }

    static class StartJob implements Runnable {

        private String device;

        private String number;

        public StartJob(String device, String number) {
            this.device = device;
            this.number = number;
        }

        @Override
        public void run() {
            try {
                System.out.println("launcher mobile " + number);
                taskHistoryMap.put(device, System.currentTimeMillis());
                exec(OPEN_MOBILE, device);
                sleep(5);
                exec(UNLOCK, device);
                sleep(3);
                exec(OPEN_TCP, device);
                sleep(3);
                exec(OPEN_MOBILE, device);
                sleep(3);
                exec(OPEN_MOBILE, device);
                sleep(4);
                exec(SET_FLAG, device);
                sleep(2);
                exec(TAP_APP, device);
                sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void exec(String command, String deviceId) {
        try {
            Process process = Runtime.getRuntime().exec(String.format(command, deviceId));
            process.waitFor(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(1000L * seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sleepMilliSeconds(int millions) {
        try {
            Thread.sleep(millions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONArray getTaskStat(String owner) {
        Map<String, String> param = new HashMap<>();
        param.put("owner", owner);
        String result = get(URL + "task/stat", param);
        return JSONObject.parseObject(result).getJSONArray("data");
    }

    private static String getTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        return DateTimePattern.format(calendar.getTime());
    }

    public static String get(String url1, Map<String, String> param) {
        HttpURLConnection connection = null;
        StringBuffer stringBuffer = new StringBuffer();
        for (String key : param.keySet()) {
            stringBuffer.append(String.format(GET_PARAM_FORMAT, key, param.get(key)));
        }
        try {
            URL url = new URL(url1 + "?" + stringBuffer);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}