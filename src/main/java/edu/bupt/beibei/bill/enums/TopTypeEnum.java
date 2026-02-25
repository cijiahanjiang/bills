package edu.bupt.beibei.bill.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liujun27
 * @since 2026/2/19 22:52
 */
public enum TopTypeEnum {

    LIFE(1, "生活"),
    FAMILY(2, "家人"),
    HEALTH(3, "健康"),
    EDUCATION(4, "教育"),
    INVESTMENT(5, "投资"),
    WORK(6, "工作"),
    SOCIAL(7, "社交");

    private final Integer value;
    private final String name;

    TopTypeEnum(int code, String name) {
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
     */
    public static String toName(Integer code) {
        for (TopTypeEnum e : values()) {
            if (e.value.equals(code)) {
                return e.name;
            }
        }
        return null;
    }

    /**
     * 文本转数字
     */
    public static Integer toValue(String label) {
        for (TopTypeEnum e : values()) {
            if (e.name.equals(label)) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * 从编码转换为枚举
     */
    public static TopTypeEnum fromValue(int code) {
        for (TopTypeEnum e : values()) {
            if (e.value == code) {
                return e;
            }
        }
        return null;
    }

    /**
     * 从文本转换为枚举
     */
    public static TopTypeEnum fromName(String label) {
        if (label == null) {
            return null;
        }
        for (TopTypeEnum e : values()) {
            if (e.name.equals(label)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 返回所有中文值列表
     *
     * @return 包含所有枚举中文标签的List
     */
    public static List<String> getAllNames() {
        return Arrays.stream(values())
                .map(TopTypeEnum::getName)
                .collect(Collectors.toList());
    }
}
