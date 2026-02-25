package edu.bupt.beibei.bill.domain;

import lombok.Data;

import java.util.List;

/**
 * @author liujun27
 * @since 2026/2/20 08:31
 */
@Data
public class UpsertBillRecord extends BillRecord {

    private List<Integer> ids;
}
