package edu.bupt.beibei.bill.dao;

import org.apache.ibatis.annotations.*;

@Mapper
public interface BillItemDAO {

    @Insert("insert into bill_item (username,items) values (#{username},#{items}) on duplicate key update items=#{items}")
    void upsert(@Param("username") String username, @Param("items") String items);

    @Select("select items from bill_item where username=#{username}")
    String getUserItems(@Param("username") String username);
}
