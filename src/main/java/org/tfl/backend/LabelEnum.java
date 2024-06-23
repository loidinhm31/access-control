package org.tfl.backend;

import java.util.Arrays;

public enum LabelEnum {
    TOP_SECRET("Top Secret", 4),
    SECRET("Secret", 3),
    CONFIDENTIAL("Confidential", 2),
    UNCLASSIFIED("Unclassified", 1);

    private String label;
    private int value;

    LabelEnum(String labelName, int labelValue) {
        this.label = labelName;
        this.value = labelValue;
    }

    public String getLabelName() {
        return label;
    }

    public int getLabelValue() {
        return value;
    }

    public static LabelEnum fromName(String labelName) {
        return Arrays.stream(LabelEnum.values())
                .filter((e) -> e.name().equals(labelName))
                .findFirst()
                .orElse(null);
    }
}
