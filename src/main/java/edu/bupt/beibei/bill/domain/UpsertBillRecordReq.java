package edu.bupt.beibei.bill.domain;

import lombok.Data;

import java.util.List;

@Data
public class UpsertBillRecordReq extends BillRecordVO {

    private List<Integer> ids;

    private String action;
}
