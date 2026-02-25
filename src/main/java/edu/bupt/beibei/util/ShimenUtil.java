package edu.bupt.beibei.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * 提示师门召唤兽所在的店铺
 */
public class ShimenUtil {

    private static final String PNG_PATH = "C:\\Users\\Administrator\\Desktop\\tmp\\11.png";

    private static String TESSERACT_COMMAND = "tesseract %s - -l chi_sim --psm 6";

    private static String INPUT_FORMAT = "adb shell input tap %d %d";

    public static void main(String[] args) {
        try {
            Process process = Runtime.getRuntime().exec("adb shell getevent |grep -e 0035 -e 0036");
            InputStream inputStream = process.getInputStream();
            for (int i = 0; i < 5; i++) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                System.out.println(reader.readLine());
                System.out.println(reader.readLine());
            }
            process.destroy();
        } catch (Exception e) {

        }
    }



    public static void main1(String[] args) {
        for (int i = 0; i < 200; i++) {
            System.out.println(i);
            try {
                tap(277, 1854, 800);
                tap(435, 392, 800);
                tap(435, 392, 800);
                tap(435, 392, 800);
//                tap(197, 392, 1000);
//                tap(197, 392, 1000);
                tap(277, 1854, 800);
                Thread.sleep(8000);
                tap(277, 1854, 800);
                tap(277, 1854, 800);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main11(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            try {
                tap(277, 1854, 800);
                tap(277, 1854, 800);
                tap(277, 1854, 800);
                tap(277, 1854, 2000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void tap(int x, int y, int sleep) {
        try {
            Process process = Runtime.getRuntime().exec(String.format(INPUT_FORMAT, x, y));
            process.waitFor();
            Thread.sleep(sleep);
        } catch (Exception e) {

        }
    }

    public static void main22(String[] args) throws Exception {
        DecimalFormat df = new DecimalFormat("#,###.00");
        Number number = df.parse("1232222456789.4649999");
        System.out.println(number.doubleValue());
    }

    public static void cut() {
        //创建一个robot对象
        try {
            Robot robut = new Robot();
            //获取屏幕分辨率
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            //打印屏幕分辨率
            System.out.println(d);
            //创建该分辨率的矩形对象
            Rectangle screenRect = new Rectangle(d);
            //根据这个矩形截图
            BufferedImage bufferedImage = robut.createScreenCapture(screenRect);
            bufferedImage = bufferedImage.getSubimage(1542 + 60, 262, 1684 - 1542 + 30, 321 - 262);
            //保存截图
            File file = new File(PNG_PATH);
            ImageIO.write(bufferedImage, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getWords() {
        try {
            String command = String.format(TESSERACT_COMMAND, PNG_PATH);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            BufferedReader subProcessInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = subProcessInputReader.readLine();

            return result;
        } catch (Exception e) {
        }
        return "error";
    }


}
