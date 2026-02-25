package edu.bupt.beibei.bill.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liujun27
 * @since 2026/2/16 07:29
 */
public enum NecessityEnum {

    MUST(1, "必须"),
    COMFORT(2, "舒适"),
    LUXURY(3, "享受"),
    UNNECESSARY(4, "浪费");


    private final Integer value;
    private final String name;

    NecessityEnum(int code, String name) {
        this.value = code;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * 数字转文本
     *
     * @param code 编码 1/2/3
     * @return 对应的文本描述，找不到返回 null
     */
    public static String toName(Integer code) {
        for (NecessityEnum e : values()) {
            if (e.value.equals(code)) {
                return e.name;
            }
        }
        return null;
    }

    /**
     * 文本转数字
     *
     * @param label 文本 "必须"/"舒适"/"享受"
     * @return 对应的编码，找不到返回 -1
     */
    public static Integer toValue(String label) {
        for (NecessityEnum e : values()) {
            if (e.name.equals(label)) {
                return e.value;
            }
        }
        return null;
    }

    public static NecessityEnum of(int code) {
        for (NecessityEnum e : values()) {
            if (e.value == code) {
                return e;
            }
        }
        return null;
    }

    public static NecessityEnum of(String name) {
        for (NecessityEnum e : values()) {
            if (e.name.equals(name)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 返回所有中文值列表
     */
    public static List<String> getAllNames() {
        return Arrays.stream(values()).map(NecessityEnum::getName).collect(Collectors.toList());
    }
}
