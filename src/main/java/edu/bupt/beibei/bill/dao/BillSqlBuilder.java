package edu.bupt.beibei.bill.dao;

import edu.bupt.beibei.bill.domain.BillRecordTableReq;
import edu.bupt.beibei.bill.domain.UpsertBillRecord;
import edu.bupt.beibei.bill.enums.FlowTypeEnum;
import edu.bupt.beibei.bill.enums.NecessityEnum;
import edu.bupt.beibei.bill.enums.RecurringEnum;
import edu.bupt.beibei.bill.enums.TopTypeEnum;
import edu.bupt.beibei.util.StringUtil;
import org.apache.ibatis.jdbc.SQL;

public class BillSqlBuilder {

    public String queryRecords(BillRecordTableReq req) {
        SQL sql = new SQL();
        sql.SELECT("*").FROM("bill_record");
        appendQueryCondition(sql, req);
        sql.ORDER_BY("deal_time");
        sql.OFFSET(req.getOffset());
        sql.LIMIT(req.getSize());
        return sql.toString();
    }

    public String countRecords(BillRecordTableReq req) {
        SQL sql = new SQL();
        sql.SELECT("count(1)").FROM("bill_record");
        appendQueryCondition(sql, req);
        return sql.toString();
    }

    private void appendQueryCondition(SQL sql, BillRecordTableReq req) {
        sql.WHERE("username='" + req.getUsername() + "'")
                .WHERE("deal_time>='" + req.getStime() + "'")
                .WHERE("deal_time<='" + req.getEtime() + " 23:59:59'");
        FlowTypeEnum flowTypeEnum = FlowTypeEnum.fromLabel(req.getFlowType());
        if (flowTypeEnum != null) {
            sql.WHERE("flow_type=" + flowTypeEnum.getValue());
        }
        RecurringEnum recurringEnum = RecurringEnum.of(req.getRecurring());
        if (recurringEnum != null) {
            sql.WHERE("recurring=" + recurringEnum.getValue());
        }
        NecessityEnum necessityEnum = NecessityEnum.of(req.getNecessity());
        if (necessityEnum != null) {
            sql.WHERE("necessity=" + necessityEnum.getValue());
        }
        if (StringUtil.notEmpty(req.getItems()) && !"全部".equals(req.getItems())) {
            sql.WHERE("items like '%" + req.getItems() + "%'");
        }
        TopTypeEnum topTypeEnum = TopTypeEnum.fromName(req.getTopType());
        if (topTypeEnum != null) {
            sql.WHERE("top_type=" + topTypeEnum.getValue());
        }
        if (StringUtil.notEmpty(req.getProduct())) {
            sql.WHERE("product like '%" + req.getProduct() + "%'");
        }
        if (StringUtil.notEmpty(req.getDealer())) {
            sql.WHERE("dealer like '%" + req.getDealer() + "%'");
        }
        if (StringUtil.isNumber(req.getMinAmount())) {
            sql.WHERE("amount>=" + Double.parseDouble(req.getMinAmount()));
        }
        if (StringUtil.isNumber(req.getMaxAmount())) {
            sql.WHERE("amount<=" + Double.parseDouble(req.getMaxAmount()));
        }
    }

    public String updateRecords(UpsertBillRecord record) {
        SQL sql = new SQL();
        sql.UPDATE("bill_record");
        if (record.getFlowType() != null) {
            sql.SET("flow_type=" + record.getFlowType());
        }
        if (record.getAmount() != null) {
            sql.SET("amount=" + record.getAmount());
        }
        if (record.getRecurring() != null) {
            sql.SET("recurring=" + record.getRecurring());
        }
        if (record.getTopType() != null) {
            sql.SET("top_type=" + record.getTopType());
        }
        if (record.getNecessity() != null) {
            sql.SET("necessity=" + record.getNecessity());
        }
        if (record.getItems() != null) {
            sql.SET("items='" + record.getItems() + "'");
        }
        if (record.getProduct() != null) {
            sql.SET("product='" + record.getProduct() + "'");
        }
        if (record.getLocation() != null) {
            sql.SET("location='" + record.getLocation() + "'");
        }
        if (record.getDealTime() != null) {
            sql.SET("deal_time='" + record.getDealTime() + "'");
        }
        if (record.getDealer() != null) {
            sql.SET("dealer='" + record.getDealer() + "'");
        }
        if (record.getDealType() != null) {
            sql.SET("deal_type='" + record.getDealType() + "'");
        }
        sql.WHERE("id in (" + StringUtil.joinInt(record.getIds(), ",") + ")");
        System.out.println(sql);
        return sql.toString();
    }
}
