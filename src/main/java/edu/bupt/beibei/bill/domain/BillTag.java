package edu.bupt.beibei.bill.domain;

import lombok.Data;

@Data
public class BillTag {

    public BillTag(String tag) {
        this.label = tag;
        this.value = tag;
    }

    private String value;

    private String label;
}
