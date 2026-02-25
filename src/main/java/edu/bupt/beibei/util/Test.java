package edu.bupt.beibei.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) throws Exception{
        System.out.println("test start");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("receive kill single");
            }
        }));
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("test run");
            Thread.sleep(2000);
        }
        System.out.println("test stop");
    }

    private static final Pattern pattern = Pattern.compile("[^0-9]");

    public static void 统计召唤兽() {
        String sourceDir = "C:\\Users\\Administrator\\Desktop\\tmp\\pic";
        try {
            File file = new File(sourceDir);
            String[] files = file.list();
            for (int i = 0; i < 10; i++) {
                String fileName = files[i];
                if (!fileName.contains("-") && fileName.contains("png")) {
                    //获取店铺名称
                }
            }
        } catch (Exception e) {

        }
    }

    public static void main2(String[] args) {
        for (int i = 1; i < 10; i++) {
            List<String> list = new LinkedList<>();
            try {
                String name = PARENT_PATH + "num\\" + i + ".png";
//                ImageUtil.cutAndSave(PARENT_PATH + i + ".png", name, 220, 503, 282 - 220, 571 - 503);
                Process process = Runtime.getRuntime().exec("tesseract " + name + " - -l chi_sim --psm 7 digits tsv");
                process.waitFor();
                BufferedReader subProcessInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s = null;
                while ((s = subProcessInputReader.readLine()) != null) {
                    list.add(s);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            for (String s : list) {
                String[] tmp = s.split("\t");
                if (tmp.length == 12 && !"level".equals(tmp[0])) {
                    System.out.println("[" + i + "]" + JSON.toJSONString(tmp));
                }
            }
        }
    }

    private static void recognizeNumber(String path) {
        try {
            List<String> list = new LinkedList<>();
            Process process = Runtime.getRuntime().exec("tesseract " + path + " - -l chi_sim --psm 7 digits tsv");
            process.waitFor();
            BufferedReader subProcessInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = null;
            while ((str = subProcessInputReader.readLine()) != null) {
                list.add(str);
            }
            for (String s : list) {
                String[] tmp = s.split("\t");
                if (tmp.length == 12 && !"level".equals(tmp[0])) {
                    System.out.println("[" + path + "]" + JSON.toJSONString(tmp));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static final String PARENT_PATH = "C:\\Users\\Administrator\\Desktop\\tmp\\pic\\";

    public static void mai2n(String[] args) throws Exception {
        String sa = FileUtil.readAsString("/Users/beibei/codes/dream/game_admin/src/main/resources/000970");
        JSONObject jsonObject = JSON.parseObject(sa);
        JSONObject resultData = jsonObject.getJSONArray("Result").getJSONObject(0).getJSONObject("DisplayData").getJSONObject("resultData");
        String data = resultData.getJSONObject("tplData").getJSONObject("result").getJSONObject("newMarketData").getString("marketData");
        String[] splits = data.split(";");
        //倒T定义：开盘收盘不超过1%，最高超过5%，最低不低于开盘/收盘0.5%
        String[] dates = new String[splits.length];
        double[] closes = new double[splits.length];
        double[] amounts = new double[splits.length];
        int index = 0;
        for (String s : splits) {
            String[] ll = s.split(",");
            String date = ll[1];
            double open = Double.valueOf(ll[2]);
            double close = Double.valueOf(ll[3]);
            double high = Double.valueOf(ll[5]);
            double low = Double.valueOf(ll[6]);
            double amount = Double.valueOf(ll[7]) / 100000000;
            dates[index] = date;
            closes[index] = close;
            amounts[index] = amount;
            index++;
            boolean j1 = low / Math.min(open, close) > 0.99;
            boolean j2 = Math.min(open, close) / Math.max(open, close) > 0.99;
            boolean j3 = high / low > 1.04;
            if (j1 && j2 && j3) {
                System.out.println(String.format("%s,open:%f,close:%f,high:%f,low:%f,%f亿", date, open, close, high, low, amount));
            }
        }
        double totalClose = 0;
        for (int i = 0; i < closes.length; i++) {
            totalClose += closes[i];
        }
        totalClose = totalClose / closes.length;
        double totalAmount = 0;

        for (int i = 0; i < closes.length; i++) {
            totalAmount += amounts[i];
        }
        totalAmount = totalAmount / closes.length;

        for (int i = 0; i < closes.length; i++) {
            closes[i] = closes[i] / totalClose * 2;
        }
        for (int i = 0; i < closes.length; i++) {
            amounts[i] = amounts[i] / totalAmount;
        }
        System.out.println(totalClose);
        System.out.println(totalAmount);
        System.out.println(JSON.toJSONString(dates));
        System.out.println(JSON.toJSONString(closes));
        System.out.println(JSON.toJSONString(amounts));
    }

    //过滤掉非中文和数字
    public static String cleanString(String str) {
        Matcher m = pattern.matcher(str);
        return m.replaceAll("").trim();
    }
}
