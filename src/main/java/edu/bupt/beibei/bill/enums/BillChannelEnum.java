package edu.bupt.beibei.bill.enums;

public enum BillChannelEnum {

    微信("wx", "微信"), 支付宝("zfb", "支付宝"), 京东("jd", "京东");

    private String code;

    private String name;

    BillChannelEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BillChannelEnum findByName(String name) {
        if ("支付宝".equals(name)) {
            return 支付宝;
        } else if ("微信".equals(name)) {
            return 微信;
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
