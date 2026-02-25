package edu.bupt.beibei.bill.service;

import edu.bupt.beibei.bill.dao.BillItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author liujun27
 * @since 2026/2/22 20:20
 */
@Service
public class BillItemService {

    @Autowired
    private BillItemDAO billItemDAO;

    public List<String> getItems(String username) {
        String items = billItemDAO.getUserItems(username);
        return Arrays.asList(items.split(","));
    }
}
