package edu.bupt.beibei.bill.dao;

import edu.bupt.beibei.bill.domain.BillRecordTableReq;
import edu.bupt.beibei.bill.domain.UpsertBillRecord;
import edu.bupt.beibei.bill.domain.UpsertBillRecordReq;
import edu.bupt.beibei.bill.domain.BillRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BillRecordDAO {

    @Insert("insert ignore into bill_record (username,flow_type,amount,recurring,top_type,items,necessity,deal_time,product,location,dealer," +
            "deal_type,deal_no) values (#{username},#{flowType},#{amount},#{recurring},#{topType},#{items},#{necessity},#{dealTime},#{product}," +
            "#{location},#{dealer},#{dealType},#{dealNo})")
    void insert(BillRecord billRecord);

    @Insert("insert ignore into bill_record (username,flow_type,amount,deal_time,product,dealer,deal_type,deal_no) values (#{username},#{flowType}," +
            "#{amount},#{dealTime},#{product},#{dealer},#{dealType},#{dealNo})")
    void insertWithDefault(BillRecord billRecord);


    @Delete("delete from bill_record where id=#{id}")
    void delete(@Param("id") int id);

    @Select("select * from bill_record where id=#{id}")
    BillRecord getById(@Param("id") int id);

    @Select("select * from bill_record where username=#{username} and deal_time>=#{stime} and deal_time<=#{etime}")
    List<BillRecord> list(@Param("username") String username, @Param("stime") String stime, @Param("etime") String etime);

    @SelectProvider(type = BillSqlBuilder.class, method = "queryRecords")
    List<BillRecord> page(BillRecordTableReq req);

    @SelectProvider(type = BillSqlBuilder.class, method = "countRecords")
    int count(BillRecordTableReq req);


    @UpdateProvider(type = BillSqlBuilder.class, method = "updateRecords")
    void updateRecords(UpsertBillRecord req);
}