package edu.bupt.beibei.bill;

import edu.bupt.beibei.bill.dao.BillRecordDAO;
import edu.bupt.beibei.bill.dao.BillItemDAO;
import edu.bupt.beibei.bill.domain.BillRecord;
import edu.bupt.beibei.bill.domain.BillRecordTableReq;
import edu.bupt.beibei.bill.domain.BillRecordVO;
import edu.bupt.beibei.bill.domain.OptionsVO;
import edu.bupt.beibei.bill.domain.PieData;
import edu.bupt.beibei.bill.domain.UpsertBillRecord;
import edu.bupt.beibei.bill.domain.UpsertBillRecordReq;
import edu.bupt.beibei.bill.enums.BillChannelEnum;
import edu.bupt.beibei.bill.enums.FlowTypeEnum;
import edu.bupt.beibei.bill.enums.NecessityEnum;
import edu.bupt.beibei.bill.enums.RecurringEnum;
import edu.bupt.beibei.bill.enums.TopTypeEnum;
import edu.bupt.beibei.bill.service.BillItemService;
import edu.bupt.beibei.bill.util.EmojiFilter;
import edu.bupt.beibei.util.FileUtil;
import edu.bupt.beibei.util.StringUtil;
import edu.bupt.beibei.util.TableUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 1.标签增删查
 * 2.上传excel并解析
 * 3.用户注册/登录（低优）
 * 4.明细数据增删改
 * 5.报表统计
 */
@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillRecordDAO billRecordDAO;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private BillItemDAO billItemDAO;

    @Autowired
    private BillItemService billItemService;

    private static final String ZFB = "zfb";

    private static final String WX = "wx";

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @GetMapping("test")
    public List<BillRecord> test() {
        BillRecordTableReq req = new BillRecordTableReq();
        return billRecordDAO.list("beibei", "2025-01-01", "2025-02-01");
    }

    @GetMapping("getSelectOptions")
    public Map getSelectOptions(String username) {
        Map<String, List<String>> result = new HashMap<>();
        result.put("topType", TopTypeEnum.getAllNames());
        result.put("items", billItemService.getItems(username));
        result.put("recurring",RecurringEnum.getAllNames());
        result.put("necessity",NecessityEnum.getAllNames());
        result.put("flowType",FlowTypeEnum.getAllNames());
        return result;
    }

    @GetMapping("items")
    public String listItem(@RequestParam String username) {
        return billItemDAO.getUserItems(username);
    }

    @GetMapping("items/update")
    public String updateTags(@RequestParam String username, @RequestParam String items) {
        billItemDAO.upsert(username, items);
        return billItemDAO.getUserItems(username);
    }

    @PostMapping("records")
    public Object getRecords(@RequestBody BillRecordTableReq req) {
        List<BillRecord> records = billRecordDAO.page(req);
        List<BillRecordVO> tableData = records.stream().map(this::build).collect(Collectors.toList());
        Map<String, Object> result = TableUtil.buildTableData(tableData);
        result.put("total", billRecordDAO.count(req));
        result.put("offset", req.getOffset());
        result.put("size", req.getSize());
        result.put("options", buildOptions(req.getUsername()));
        return result;
    }


    @PostMapping("records/upsert")
    public Object upsertRecords(@RequestBody UpsertBillRecordReq req) {
        if ("edit".equals(req.getAction())) {
            billRecordDAO.updateRecords(buildUpsertRecord(req));
        } else if ("create".equals(req.getAction())) {
            billRecordDAO.insert(buildUpsertRecord(req));
        }
        return "success";
    }

    private UpsertBillRecord buildUpsertRecord(UpsertBillRecordReq req) {
        UpsertBillRecord billRecord = new UpsertBillRecord();
        BeanUtils.copyProperties(req, billRecord);
        if (req.getItems() != null) {
            billRecord.setItems(StringUtil.join(req.getItems(), ","));
        }
        billRecord.setRecurring(RecurringEnum.toValue(req.getRecurring()));
        billRecord.setFlowType(FlowTypeEnum.toValue(req.getFlowType()));
        billRecord.setTopType(TopTypeEnum.toValue(req.getTopType()));
        billRecord.setNecessity(NecessityEnum.toValue(req.getNecessity()));
        return billRecord;
    }

    @GetMapping("records/delete")
    public String deleteRecord(@RequestParam String username, @RequestParam int id) {
        BillRecord billRecord = billRecordDAO.getById(id);
        if (billRecord == null || !billRecord.getUsername().equals(username)) {
            return "false";
        }
        billRecordDAO.delete(id);
        return "success";
    }

    @PostMapping("stat")
    public Object stat(@RequestBody BillRecordTableReq req) {
        List<BillRecord> records = billRecordDAO.list(req.getUsername(), req.getStime(), req.getEtime());
        Map<String, Double> incomeMonthMap = new HashMap<>();
        Map<String, Double> incomeTagMap = new HashMap<>();

        Map<String, Double> outcomeMonthMap = new HashMap<>();
        Map<Integer, Double> outcomeTopTypeMap = new HashMap<>();
        Map<String, Double> outcomeTagMap = new HashMap<>();
        Map<Integer, Double> outcomeNecessityMap = new HashMap<>();
        for (BillRecord record : records) {
            countOutcomeTopTypeMap(outcomeTopTypeMap, record);
            countOutcomeTagMap(outcomeTagMap, record);
            countOutcomeNecessityMap(outcomeNecessityMap, record);
            countIncomeMonthMap(incomeMonthMap, record);
            countOutMonthMap(outcomeMonthMap, record);
            countIncomeTagMap(incomeTagMap, record);
        }
        List<String> months = getMonths(req.getStime(), req.getEtime());
        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("monthIncomes", buildLineData(months, incomeMonthMap));
        result.put("monthOutcomes", buildLineData(months, outcomeMonthMap));
        result.put("incomeTags", buildPieData(incomeTagMap));
        result.put("outcomeTopTypes", buildTopTypePieData(outcomeTopTypeMap));
        result.put("outcomeTags", buildPieData(outcomeTagMap));
        result.put("outcomeNecessity", buildNecessityPieData(outcomeNecessityMap));
        return result;
    }

    @PostMapping("uploadRecords")
    //导入数据并初始化
    public String uploadRecord(@RequestParam String username, @RequestBody MultipartFile file, @RequestParam String channel) {
        long l1 = System.currentTimeMillis();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        BillRecordDAO billRecordDAO = sqlSession.getMapper(BillRecordDAO.class);
        BillChannelEnum channelEnum = BillChannelEnum.findByName(channel);
        if (channelEnum == null) {
            return "error";
        }
        List<String> records = null;
        if (channelEnum == BillChannelEnum.微信) {
            records = FileUtil.readByLine(file);
        } else if (channelEnum == BillChannelEnum.支付宝) {
            records = FileUtil.readByLine(file, "GBK");
        }
        System.out.println(System.currentTimeMillis() - l1);
        l1 = System.currentTimeMillis();
        if (records == null) {
            return "success";
        }
        for (String recordStr : records) {
            BillRecord record = parse(recordStr, channelEnum.getCode());
            if (record != null) {
                record.setUsername(username);
                billRecordDAO.insertWithDefault(record);
            }
        }
        sqlSession.commit();
        sqlSession.clearCache();
        System.out.println(System.currentTimeMillis() - l1);
        return "success";
    }

    private OptionsVO buildOptions(String username) {
        OptionsVO optionsVO = new OptionsVO();
        optionsVO.setRecurring(RecurringEnum.getAllNames());
        optionsVO.setItems(parseTags(billItemDAO.getUserItems(username)));
        optionsVO.setNecessity(NecessityEnum.getAllNames());
        optionsVO.setFlowType(FlowTypeEnum.getAllNames());
        optionsVO.setTopType(TopTypeEnum.getAllNames());
        return optionsVO;
    }

    private List<String> buildLineData(List<String> months, Map<String, Double> map) {
        List<String> result = new LinkedList<>();
        for (String month : months) {
            result.add(decimalFormat.format(map.getOrDefault(month, 0d)));
        }
        return result;
    }

    private List<PieData> buildTopTypePieData(Map<Integer, Double> map) {
        List<PieData> result = new LinkedList<>();
        for (Integer key : map.keySet()) {
            result.add(new PieData(TopTypeEnum.toName(key), decimalFormat.format(map.get(key))));
        }
        return result;
    }

    private List<PieData> buildNecessityPieData(Map<Integer, Double> map) {
        List<PieData> result = new LinkedList<>();
        for (Integer key : map.keySet()) {
            result.add(new PieData(NecessityEnum.toName(key), decimalFormat.format(map.get(key))));
        }
        return result;
    }

    private List<PieData> buildPieData(Map<String, Double> map) {
        List<PieData> result = new LinkedList<>();
        for (String key : map.keySet()) {
            result.add(new PieData(key, decimalFormat.format(map.get(key))));
        }
        return result;
    }

    private void countOutMonthMap(Map<String, Double> map, BillRecord record) {
        if (!FlowTypeEnum.EXPENSE.getValue().equals(record.getFlowType())) {
            return;
        }
        String key = record.getDealTime().substring(0, 7);
        double d = map.getOrDefault(key, 0.0);
        map.put(key, d + record.getAmount());
    }

    private void countIncomeMonthMap(Map<String, Double> map, BillRecord record) {
        if (!FlowTypeEnum.INCOME.getValue().equals(record.getFlowType())) {
            return;
        }
        String key = record.getDealTime().substring(0, 7);
        double d = map.getOrDefault(key, 0.0);
        map.put(key, d + record.getAmount());
    }

    private void countIncomeTagMap(Map<String, Double> map, BillRecord record) {
        if (!FlowTypeEnum.INCOME.getValue().equals(record.getFlowType())) {
            return;
        }
        String[] tags = record.getItems() == null ? null : record.getItems().split(",");
        if (tags != null) {
            for (String tag : tags) {
                double d = map.getOrDefault(tag, 0d);
                map.put(tag, d + record.getAmount());
            }
        }
    }

    private void countOutcomeTopTypeMap(Map<Integer, Double> map, BillRecord record) {
        if (!FlowTypeEnum.EXPENSE.getValue().equals(record.getFlowType())) {
            return;
        }
        int key = record.getTopType();
        double d = map.getOrDefault(key, 0d);
        map.put(key, d + record.getAmount());
    }


    private void countOutcomeTagMap(Map<String, Double> map, BillRecord record) {
        if (!FlowTypeEnum.EXPENSE.getValue().equals(record.getFlowType())) {
            return;
        }
        String[] tags = record.getItems() == null ? null : record.getItems().split(",");
        if (tags != null) {
            for (String tag : tags) {
                double d = map.getOrDefault(tag, 0d);
                map.put(tag, d + record.getAmount());
            }
        }
    }

    private void countOutcomeNecessityMap(Map<Integer, Double> map, BillRecord record) {
        if (!FlowTypeEnum.EXPENSE.getValue().equals(record.getFlowType())) {
            return;
        }
        int key = record.getNecessity();
        double d = map.getOrDefault(key, 0d);
        map.put(key, d + record.getAmount());
    }

    /**
     * @param stime 2023-01-01
     * @param etime 2023-03-01
     * @return [2023-01,2023-02,2023-03]
     */
    private List<String> getMonths(String stime, String etime) {
        List<String> result = new LinkedList<>();
        String e = etime.substring(0, 7);
        int year = Integer.parseInt(stime.substring(0, 4));
        int month = Integer.parseInt(stime.substring(5, 7));
        String time = formatTime(year, month);
        while (!e.equals(time)) {
            result.add(time);
            if (month < 12) {
                month++;
            } else {
                year++;
                month = 1;
            }
            time = formatTime(year, month);
        }
        result.add(time);
        return result;
    }

    private String formatTime(int year, int month) {
        return year + "-" + (month > 9 ? month : ("0" + month));
    }

    public static void main(String[] args) {
        System.out.println(Integer.parseInt("01"));
    }


    private BillRecordVO build(BillRecord record) {
        BillRecordVO recordVO = new BillRecordVO();
        recordVO.setId(record.getId());
        recordVO.setUsername(record.getUsername());
        recordVO.setAmount(record.getAmount());
        recordVO.setRecurring(RecurringEnum.toName(record.getRecurring()));
        recordVO.setFlowType(FlowTypeEnum.codeToLabel(record.getFlowType()));
        recordVO.setTopType(TopTypeEnum.toName(record.getTopType()));
        recordVO.setItems(Arrays.asList(record.getItems().split(",")));
        recordVO.setNecessity(NecessityEnum.toName(record.getNecessity()));
        recordVO.setProduct(shapeString(record.getProduct(), 20));
        recordVO.setDealer(shapeString(record.getDealer(), 20));
        recordVO.setLocation(record.getLocation());
        recordVO.setDealTime(record.getDealTime());
        recordVO.setDealType(record.getDealType());
        recordVO.setDealNo(record.getDealNo());
        return recordVO;
    }

    private List<String> parseTags(String tags) {
        if (StringUtil.notEmpty(tags)) {
            return Arrays.asList(tags.split(","));
        } else {
            return new ArrayList<>();
        }
    }


    private String shapeString(String str, int max) {
        if (str == null || str.length() < max) {
            return str;
        }
        return str.substring(0, max);
    }

    //解析微信账单
    private BillRecord parse(String data, String channel) {
        switch (channel) {
            case WX:
                return parseWx(data);
            case ZFB:
                return parseZfb(data);
            default:
                return null;
        }
    }

    private BillRecord parseWx(String data) {
        try {
            if (data.contains("已全额退款") || data.contains("对方已退还")) {
                return null;
            }
            int index1 = data.indexOf(",");
            String time = data.substring(0, index1);
            if (time.length() > 15 && time.contains("-")) {
                BillRecord detail = new BillRecord();
                detail.setDealTime(time);
                int index2 = data.indexOf(",", index1 + 1);
                detail.setDealType(data.substring(index1 + 1, index2));
                String[] tmp = data.split(",¥");
                String[] ss = tmp[1].split(",");
                detail.setAmount(Double.parseDouble(ss[0].trim()));
                detail.setDealNo(WX + "_" + ss[3].trim());
                int index = tmp[0].lastIndexOf(",");
                if ("收入".equals(tmp[0].substring(index + 1).trim())) {
                    detail.setFlowType(1);
                } else {
                    detail.setFlowType(0);
                }
                String[] kk = tmp[0].split(",");
                if (kk.length == 5) {
                    detail.setDealer(kk[2]);
                    detail.setProduct(EmojiFilter.filterEmoji(kk[3]).replace("\"", ""));
                } else {
                    detail.setDealer(kk[kk.length - 2]);
                    detail.setProduct(EmojiFilter.filterEmoji(kk[kk.length - 3]).replace("\"", ""));
                }
                return detail;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private BillRecord parseZfb(String recordStr) {
        try {
            if (recordStr.startsWith("---")) {
                return null;
            }
            String[] ll = recordStr.split(",");
            String stat = ll[11].trim();
            if (stat.equals("退款成功") || stat.equals("交易关闭")) {
                return null;
            }
            if ("资金转移".equals(ll[15].trim())) {
                return null;
            }
            BillRecord record = new BillRecord();
            record.setDealTime(ll[2].trim());
            record.setDealNo(ZFB + ll[0].trim());
            record.setDealer(ll[7].trim());
            record.setProduct(ll[8].trim());
            record.setAmount(Double.parseDouble(ll[9].trim()));
            record.setDealType(ll[6].trim());
            //判断收支
            if (ll[15].contains("收入")) {
                record.setFlowType(1);
            } else {
                record.setFlowType(0);
            }
            return record;
        } catch (Exception e) {
            return null;
        }
    }
}
