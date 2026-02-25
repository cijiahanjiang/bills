package edu.bupt.beibei.bill.domain;

import lombok.Data;

import java.util.List;

@Data
public class BillRecordVO {

    private int id;

    private String username;

    private String flowType;

    private Double amount;

    private String recurring;

    private String topType;

    private String necessity;

    private List<String> items;

    private String dealTime;

    private String product;

    private String location;

    private String dealType;

    private String dealer;

    private String dealNo;
}
