package edu.bupt.beibei.bill.enums;

/**
 * @author liujun27
 * @since 2026/2/19 23:06
 */

import java.util.Arrays;
import java.util.List;

/**
 * 收支类型枚举
 */
public enum FlowTypeEnum {

    EXPENSE(0, "支出"), INCOME(1, "收入");

    private final Integer value;
    private final String name;

    FlowTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * 数字转文本
     */
    public static String codeToLabel(int code) {
        for (FlowTypeEnum e : values()) {
            if (e.value == code) {
                return e.name;
            }
        }
        return null;
    }

    /**
     * 文本转数字
     */
    public static Integer toValue(String label) {
        for (FlowTypeEnum e : values()) {
            if (e.name.equals(label)) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * 从编码转换为枚举
     */
    public static FlowTypeEnum fromCode(int code) {
        for (FlowTypeEnum e : values()) {
            if (e.value == code) {
                return e;
            }
        }
        return null;
    }

    /**
     * 从文本转换为枚举
     */
    public static FlowTypeEnum fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (FlowTypeEnum e : values()) {
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
        return Arrays.stream(values()).map(FlowTypeEnum::getName).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 判断是否为收入
     */
    public boolean isIncome() {
        return this == INCOME;
    }

    /**
     * 判断是否为支出
     */
    public boolean isExpense() {
        return this == EXPENSE;
    }
}