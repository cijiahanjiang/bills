package edu.bupt.beibei.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

//目的在于构造索引
public class ImageUtil {

    public static void main(String[] args) throws Exception {
//        String pic1 = "C:\\codes\\my\\colorimage\\pic\\20220916-041236.png";
//        String pic = "C:\\codes\\my\\colorimage\\抓鬼\\y0-2-1.jpg";
//
//        List<int[][]> colors = cut(pic);
//        int[][] image1 = colors.get(9);
//        System.out.println(image1[5][5]);
//        colors = cut(pic1);
//        int[][] image2 = colors.get(9);
//        System.out.println(image2[5][5]);
//        System.out.println(JSON.toJSONString(getPrint(image1)));
//        System.out.println(JSON.toJSONString(getPrint(image2)));
////        System.out.println(JSON.toJSONString(getPrint(images.get(6))));
        buildIndex();
    }

    /**
     * @param originPath 源图片
     * @param dest       新图片
     * @param x
     * @param y
     * @param x2
     * @param y2
     */
    public static void cutAndSave(String originPath, String dest, int x, int y, int x2, int y2) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(originPath));
            BufferedImage sub = bufferedImage.getSubimage(x, y, x2-x, y2-y);
            File file = new File(dest);
            file.createNewFile();
            ImageIO.write(sub, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteImage(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        return true;
    }

    public static String encode(String fileName) throws Exception {
        byte[] data = null;
        FileInputStream in = new FileInputStream(fileName);
        data = new byte[in.available()];
        in.read(data);
        in.close();
        return Base64Util.encode(data);
    }

    public static List<ImageRecognize> getTags(List<int[][]> colors) {
        List<ImagePrint> index = buildIndex();
        List<ImageRecognize> result = new LinkedList<>();
        for (int i = 0; i < colors.size(); i++) {
            //区分背景
            int type = (i / 4 + i % 4) % 2;
            result.add(getShortestDistance(index, colors.get(i), type));
        }
        return result;
    }

    public static List<ImagePrint> buildIndex() {
        String dirs = "C:\\codes\\my\\colorimage\\抓鬼";
        File file = new File(dirs);
        List<ImagePrint> list = new LinkedList<>();
        if (file.exists()) {
            String[] strings = file.list();
            for (String s : strings) {
                String pic = dirs + "\\" + s;
                List<int[][]> images = cut(pic);
                String[] tmp = s.split("-");
                int index = 4 * Integer.parseInt(tmp[1]) + Integer.parseInt(tmp[2].split("\\.")[0]);
                int type = (index / 4 + index % 4) % 2;
                ImagePrint print = getPrint(images.get(index));
                print.setType(type);
                if (s.startsWith("j")) {
                    print.getTags().add("僵尸");
                }
                if (s.startsWith("k")) {
                    print.getTags().add("骷髅");
                }
                if (s.startsWith("m")) {
                    print.getTags().add("马面");
                }
                if (s.startsWith("n")) {
                    print.getTags().add("牛头");
                }
                if (s.startsWith("y")) {
                    print.getTags().add("野鬼");
                }
                list.add(print);
            }
        }
        String json = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(json);
        FileUtil.append("C:\\codes\\game_ac_mg\\src\\main\\resources\\index_zhuagui.txt", json);
        return list;
    }

    //从左到右，从上到下，切16张小图
    public static List<int[][]> cut(String file) {
        List<int[][]> result = new LinkedList<>();
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(file));
            int x = 76;
            int y = 563;
            int width = 208;

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int startX = x + 2 + 211 * (j % 4);
                    int startY = y + 2 + 211 * (i % 4);
                    int[][] tmp = new int[width][width];
                    for (int l1 = 0; l1 < width; l1++) {
                        for (int l2 = 0; l2 < width; l2++) {
                            tmp[l1][l2] = bufferedImage.getRGB(startX + l1, startY + l2);
                        }
                    }
                    result.add(tmp);
                }
            }
            return result;
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * @return DistanceResult 是否图形匹配、颜色匹配、匹配的图形指纹、匹配的图片
     */
    public static ImageRecognize getShortestDistance(List<ImagePrint> indexes, int[][] color, int type) {
        ImagePrint imagePrint = getPrint(color);

        ImageRecognize imageRecognize = new ImageRecognize();
        imageRecognize.originPrint = imagePrint;
        if (indexes.size() == 0) {
            return imageRecognize;
        }
        List<String> prints = getAllDirectionShapePrint(imagePrint.getShapePrint());
        for (ImagePrint index : indexes) {
            if (index.getType() == type) {
                int shapeDistance = getMinShapeDistance(prints, index.getShapePrint());
                if (shapeDistance < 5) {
                    int colorDistance = ImageUtil.getColorDistance(index.getColorPrint(), imagePrint.getColorPrint());
                    if (colorDistance < 4) {
                        //取最佳结果
                        if (colorDistance + shapeDistance < imageRecognize.distance) {
                            imageRecognize.distance = colorDistance + shapeDistance;
                            imageRecognize.match = true;
                            imageRecognize.matchPrint = index;
                        }
                    }
                }
            }
        }
        return imageRecognize;
    }

    //旋转90度
    private static int[][] rotateArray(int[][] request) {
        int[][] result = new int[request[0].length][request.length];
        int h = request.length;
        for (int i = 0; i < request.length; i++) {
            for (int j = 0; j < request[0].length; j++) {
                result[j][h - 1 - i] = request[i][j];
            }
        }
        return result;
    }

    public static ImagePrint getPrint(int[][] originColors) {
        String shapePrints = getShapePrint(originColors);
        int[] colors = getColorPrintV2(originColors);
        ImagePrint imagePrint = new ImagePrint();
        imagePrint.setShapePrint(shapePrints);
        imagePrint.setColorPrint(colors);
        imagePrint.setTags(new LinkedList<>());
        return imagePrint;
    }


    //必须正方形，宽为8的倍数
    private static String getShapePrint(int[][] originColors) {
        StringBuilder stringBuffer = new StringBuilder();
        int x = originColors.length;
        int y = originColors[0].length;
        int[][][] result = new int[8][8][3];
        int[][] result_count = new int[8][8];
        int count = 0;
        int[] colors = new int[3];
        int step = x / 8;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int color = originColors[i][j];
                int[] tmp = ImageUtil.parseRGB(color);
                count++;
                result_count[i / step][j / step]++;
                result[i / step][j / step][0] += tmp[0];
                result[i / step][j / step][1] += tmp[1];
                result[i / step][j / step][2] += tmp[2];
                colors[0] += tmp[0];
                colors[1] += tmp[1];
                colors[2] += tmp[2];
            }
        }
        int averageColor = (colors[0] + colors[1] + colors[2]) / count;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (result_count[i][j] > 0) {
                    stringBuffer.append((result[i][j][0] + result[i][j][1] + result[i][j][2]) / result_count[i][j] > averageColor ? 1 : 0);
                } else {
                    stringBuffer.append(0);
                }
            }
        }
        return stringBuffer.toString();
    }

    //抓鬼用，不能排除底色，有bug
    private static int[] getColorPrintV2(int[][] originColors) {
        try {
            int count = 0;
            int[] colors = new int[3];
            for (int[] originColor : originColors) {
                for (int color : originColor) {
                    //排除底色和纯黑色
                    count++;
                    int[] tmp = ImageUtil.parseRGB(color);
                    colors[0] += tmp[0];
                    colors[1] += tmp[1];
                    colors[2] += tmp[2];
                }
            }
            colors[0] = colors[0] / count;
            colors[1] = colors[1] / count;
            colors[2] = colors[2] / count;
            return colors;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //师门用
    private static int[] getColorPrint(int[][] originColors) {
        try {
            int count = 0;
            int[] colors = new int[3];
            for (int[] originColor : originColors) {
                for (int color : originColor) {
                    //排除底色和纯黑色
                    if (color != -8347177 && color != -7428912 && color != -16777216) {
                        count++;
                        int[] tmp = ImageUtil.parseRGB(color);
                        colors[0] += tmp[0];
                        colors[1] += tmp[1];
                        colors[2] += tmp[2];
                    }
                }
            }
            colors[0] = colors[0] / count;
            colors[1] = colors[1] / count;
            colors[2] = colors[2] / count;
            return colors;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int[] parseRGB(int pixel) {
        int[] rgb = new int[3];
        rgb[0] = (pixel & 0xff0000) >> 16;
        rgb[1] = (pixel & 0xff00) >> 8;
        rgb[2] = (pixel & 0xff);
        return rgb;
    }

    private static int getHDistance(String s1, String s2) {
        if (s1.length() > 63) {
            s1 = s1.substring(0, 63);
        }
        if (s2.length() > 63) {
            s2 = s2.substring(0, 63);
        }
        long l1 = Long.parseLong(s1, 2);
        long l2 = Long.parseLong(s2, 2);
        return Long.bitCount(l1 ^ l2);
    }

    //增加斜率
    private static int getColorDistance(int[] color1, int[] color2) {
        return Math.abs(color1[0] - color2[0]) / 5 * Math.abs(color1[0] - color2[0]) / 5 + Math.abs(color1[1] - color2[1]) / 5 * Math.abs(color1[1] - color2[1]) / 5 + Math.abs(color1[2] - color2[2]) / 5 * Math.abs(color1[2] - color2[2]) / 5;
    }

    private static int getMinShapeDistance(List<String> list, String print) {
        int result = Integer.MAX_VALUE;
        for (String s : list) {
            int i = ImageUtil.getHDistance(s, print);
            result = Math.min(result, i);
        }
        return result;
    }

    private static List<String> getAllDirectionShapePrint(String shapePrint) {
        List<String> result = new LinkedList<>();
        result.add(shapePrint);
        int[][] tmp = new int[8][8];

        char[] chars = shapePrint.toCharArray();
        for (int i = 0; i < 64; i++) {
            tmp[i / 8][i % 8] = chars[i] - '0';
        }
        for (int i = 0; i < 3; i++) {
            tmp = ImageUtil.rotateArray(tmp);
            result.add(arrayToString(tmp));
        }
        return result;
    }

    private static String arrayToString(int[][] result) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                stringBuilder.append(result[i][j]);
            }
        }
        return stringBuilder.toString();
    }


    private static class ImageRecognize {
        public ImagePrint originPrint;

        public ImagePrint matchPrint;

        public boolean match;

        public int distance = 100;
    }

    private static class ImagePrint {
        private int id;

        private String location;

        private String shapePrint;

        private int[] colorPrint;

        private List<String> tags;

        private int type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getShapePrint() {
            return shapePrint;
        }

        public void setShapePrint(String shapePrint) {
            this.shapePrint = shapePrint;
        }

        public int[] getColorPrint() {
            return colorPrint;
        }

        public void setColorPrint(int[] colorPrint) {
            this.colorPrint = colorPrint;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static boolean save(String fileName, String base64) {
        try {
            File file = new File(fileName);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] data = Base64Util.decode(base64);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}


