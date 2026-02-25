package edu.bupt.beibei.bill.domain;

import lombok.Data;

@Data
public class BillRecord {

    private Integer id;

    private String username;

    private Integer flowType;

    private Double amount;

    private Integer recurring;

    private Integer topType;

    private String items;

    private Integer necessity;

    private String dealTime;

    private String product;

    private String location;

    private String dealer;

    private String dealType;

    private String dealNo;
}
