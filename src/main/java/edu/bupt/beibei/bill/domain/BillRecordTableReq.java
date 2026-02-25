package edu.bupt.beibei.bill.domain;

import lombok.Data;

@Data
public class BillRecordTableReq {

    private String username;

    private String stime;

    private String etime;

    private String dealer;

    private String product;

    private String flowType;

    private String recurring;

    private String topType;

    private String necessity;

    private String minAmount;

    private String maxAmount;

    private String items;

    private int offset;

    private int size;
}
