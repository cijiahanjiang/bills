package edu.bupt.beibei.bill.domain;

import lombok.Data;

import java.util.List;

/**
 * @author liujun27
 * @since 2026/2/19 22:57
 */

/**
 * 前端下拉框选项返回类
 */
@Data
public class OptionsVO {

    private List<String> necessity;
    private List<String> topType;
    private List<String> items;
    private List<String> flowType;
    private List<String> recurring;
}
