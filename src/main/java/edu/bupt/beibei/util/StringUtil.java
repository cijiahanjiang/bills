package edu.bupt.beibei.util;

import java.util.List;

public class StringUtil {

    public static boolean isNumber(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static String join(List<String> list, String split) {
        StringBuffer stringBuffer = new StringBuffer();
        if (list == null || list.size() == 0) {
            return "";
        }
        for (String s : list) {
            stringBuffer.append(s).append(",");
        }
        return stringBuffer.substring(0, stringBuffer.length() - 1);
    }

    public static String joinInt(List<Integer> list, String split) {
        StringBuffer stringBuffer = new StringBuffer();
        if (list == null || list.size() == 0) {
            return "";
        }
        for (Integer s : list) {
            stringBuffer.append(s).append(",");
        }
        return stringBuffer.substring(0, stringBuffer.length() - 1);
    }

    public static boolean notEmpty(String str) {
        return !"".equals(str) && str != null;
    }

    public static int getRandom(int i) {
        return (int) (Math.random() * i);
    }
}
