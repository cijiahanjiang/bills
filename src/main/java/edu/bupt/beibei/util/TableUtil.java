package edu.bupt.beibei.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableUtil {

    public static Map<String, Object> buildTableData(List list) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", list);
        return map;
    }
}
