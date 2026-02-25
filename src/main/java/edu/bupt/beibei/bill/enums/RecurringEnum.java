package edu.bupt.beibei.bill.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liujun27
 * @since 2026/2/21 19:39
 */
public enum RecurringEnum {

    FLEXIBLE(0, "临时"),
    FIXED(1, "长期");

    private final int value;
    private final String name;

    RecurringEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据值获取名称
     */
    public static String toName(int value) {
        for (RecurringEnum e : values()) {
            if (e.value == value) {
                return e.name;
            }
        }
        return null;
    }

    /**
     * 根据名称获取值
     */
    public static Integer toValue(String name) {
        for (RecurringEnum e : values()) {
            if (e.name.equals(name)) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * 根据值获取枚举
     */
    public static RecurringEnum of(int value) {
        for (RecurringEnum e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }

    /**
     * 从文本转换为枚举
     */
    public static RecurringEnum of(String label) {
        if (label == null) {
            return null;
        }
        for (RecurringEnum e : values()) {
            if (e.name.equals(label)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 返回所有中文值列表
     */
    public static List<String> getAllNames() {
        return Arrays.stream(values()).map(RecurringEnum::getName).collect(Collectors.toList());
    }
}
